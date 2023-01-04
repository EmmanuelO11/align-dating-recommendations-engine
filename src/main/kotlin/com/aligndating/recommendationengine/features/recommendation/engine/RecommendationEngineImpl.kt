package com.aligndating.recommendationengine.features.recommendation.engine

import com.aligndating.recommendationengine.extensions.getLogger
import com.aligndating.recommendationengine.features.accounts.data.User
import com.aligndating.recommendationengine.features.accounts.source.AccountsDataSource
import com.aligndating.recommendationengine.features.likes.source.dislike.DislikeDataSource
import com.aligndating.recommendationengine.features.likes.source.like.LikeDataSource
import com.aligndating.recommendationengine.features.location.data.UserLocation
import com.aligndating.recommendationengine.features.location.source.LocationDataSource
import com.aligndating.recommendationengine.features.recommendation.data.PersistableUserRecommendations
import com.aligndating.recommendationengine.features.recommendation.data.Recommendation
import com.aligndating.recommendationengine.features.recommendation.data.RecommendationHolder
import com.aligndating.recommendationengine.features.recommendation.source.UserRecommendationsDataSource
import com.aligndating.recommendationengine.features.recommendation.utils.HaversineAlgorithm
import com.aligndating.recommendationengine.features.recommendation.utils.normalize
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import redis.clients.jedis.Jedis
import kotlin.math.abs

class RecommendationEngineImpl(
    private val accountsDataSource: AccountsDataSource,
    private val locationDataSource: LocationDataSource,
    private val dislikeDataSource: DislikeDataSource,
    private val likeDataSource: LikeDataSource,
    private val recommendationsDataSource: UserRecommendationsDataSource,
    private val redisUri: String
) : RecommendationEngine {

    companion object {
        private const val ONE_DAY_TIMESTAMP = 86_400_000L
        private const val DISLIKE_TIME_EXPIRY = 14 * 86_400_000L // 14 days
        private const val DISTANCE_WEIGHT = 0.40
        private const val AGE_WEIGHT = 0.30
        private const val ACTIVE_SCORE_WEIGHT = 0.30
        private const val BOOST_FACTOR = 1.33
        private const val MAXIMUM_DISTANCE = 100000
    }

    private val logger = getLogger()

    override suspend fun computeForUsersInArea(
        lat: Double,
        lng: Double,
        excludeUsers: List<String>
    ) {
        // 1: get users in this area
        val locations = locationDataSource.getUsersNearby(
            Point(
                Position(
                    lat,
                    lng
                )
            ),
            MAXIMUM_DISTANCE
        )
        locations.forEach {
            if (!excludeUsers.contains(it.userId)) {
                computeAndSave(
                    userId = it.userId,
                    update = true
                )
            }
        }
    }

    override suspend fun computeAndSave(
        userId: String,
        update: Boolean
    ) {
        val recommendationsList = computeRecommendationForUser(
            userId, accountsDataSource, locationDataSource, dislikeDataSource, likeDataSource, redisUri
        )
        if (recommendationsList == null) {
            logger.info("no recommendations found for user $userId")
        } else {
            val recommendations = PersistableUserRecommendations(
                userId, recommendationsList
            )
            saveUserRecommendations(recommendations, recommendationsDataSource, update)
        }
    }

    override suspend fun getUserLocationAndComputeInArea(userId: String) {
        val location = locationDataSource.getUserLocation(userId)?.toCurrentLocations()
        if (location == null) {
            logger.info("location not found for user $userId")
        } else {
            computeForUsersInArea(
                lat = location.latitude,
                lng = location.longitude,
                excludeUsers = listOf(userId)
            )
        }
    }

    private suspend fun computeRecommendationForUser(
        userId: String,
        accountsDataSource: AccountsDataSource,
        locationDataSource: LocationDataSource,
        dislikeDataSource: DislikeDataSource,
        likeDataSource: LikeDataSource,
        redisUri: String
    ): List<Recommendation>? {
        // 1: get this user location
        val currentLocation = locationDataSource.getUserLocation(userId) ?: return null
        // 2: get user distance preference
        val currentUser = accountsDataSource.getUserById(userId) ?: return null
        // 2: get nearby user
        val maxDistance = currentUser.userPreferences.maximumDistance
        val nearbyUsers = locationDataSource.getUsersNearby(
            Point(
                Position(
                    currentLocation.location.coordinates[0],
                    currentLocation.location.coordinates[1]
                )
            ), maxDistance = maxDistance
        ).toMutableList()
        nearbyUsers.removeIf { it.userId == userId }
        // 3: compute recommendations
        val recommendations = mutableListOf<RecommendationHolder>()
        val potentialUsers = accountsDataSource.getUsers(ids = nearbyUsers.map { it.userId })
            .filter {
                it.userPreferences.accountVisibility
            }.filter {
                currentUser.userPreferences.partnerInterests.contains(it.userInformation.gender) &&
                        it.userPreferences.partnerInterests.contains(currentUser.userInformation.gender)
            }
//        logger.info("Potential Users: ${potentialUsers.map { it.profileID }}")
        val usersWithSuperLikes = likeDataSource.getUserShootingStarLikes(userId)
        val jedisClient = Jedis(redisUri)
        potentialUsers.forEach { targetUser ->
            // i: check if user A has already liked user B, if yes, don't show
//            val hash = HashingUtils.generateHash(userA = userId, userB = targetUser.profileID)
            // TODO: this can be optimized by getting the user likes only once, saving fetch trips
            val alreadyLiked =
                likeDataSource.checkIfUserAHasAlreadyLikedUserB(userA = userId, userB = targetUser.profileID)
            if (alreadyLiked) return@forEach
            // ii: check if userA has already disliked userB
            // if yes, then don't show it in the time limit(14 days by default)
            val dislike = dislikeDataSource.checkIfUserAHasAlreadyDislikedUserB(userId, targetUser.profileID)
            if (dislike != null) {
                if (dislike.timestamp + DISLIKE_TIME_EXPIRY > System.currentTimeMillis()) {
                    return@forEach
                }
            }
            val isBoosted = jedisClient.get("boost_${targetUser.profileID}") != null
            // compute recommendation
            val recommendation = computeScore(
                nearbyUsers = nearbyUsers,
                currentLocation = currentLocation,
                targetUser = targetUser,
                maxDistance = maxDistance,
                currentUser = currentUser,
                isSuperLiked = false,
                isBoosted = isBoosted
            ) ?: return@forEach
            recommendations.add(recommendation)
        }
        usersWithSuperLikes.forEach { superLiked ->
            // 1: get user
            val targetUser = accountsDataSource.getUserById(superLiked.likedBy) ?: return@forEach
            // 2: compute score
            val recommendation = computeScore(
                nearbyUsers = nearbyUsers,
                currentLocation = currentLocation,
                targetUser = targetUser,
                maxDistance = maxDistance,
                currentUser = currentUser,
                isSuperLiked = true,
                isBoosted = jedisClient.get("boost_${targetUser.profileID}") != null
            ) ?: return@forEach
            recommendations.add(recommendation)
            // 3: remove super like
            likeDataSource.removeLike(id = superLiked.id)
        }
        return recommendations.map {
            if (it.isBoosted) it.apply {
                this.recommendation.score = this.recommendation.score * BOOST_FACTOR
            } else it
        }
            .sortedBy { it.recommendation.score }
            .reversed()
            .map { it.recommendation }
    }

    private fun computeScore(
        nearbyUsers: List<UserLocation>,
        currentLocation: UserLocation,
        targetUser: User,
        maxDistance: Int,
        currentUser: User,
        isSuperLiked: Boolean,
        isBoosted: Boolean,
    ): RecommendationHolder? {
        logger.info("Compute score call")
        // calculate distance score
        val location = nearbyUsers.find { it.userId == targetUser.profileID } ?: return null
        val targetLongitude = location.location.coordinates[0]
        val targetLatitude = location.location.coordinates[1]
        logger.info("Target Coordinates: ($targetLatitude, $targetLongitude)")
        val currentLongitude = currentLocation.location.coordinates[0]
        val currentLatitude = currentLocation.location.coordinates[1]
        logger.info("Current Coordinates: ($currentLatitude, $currentLongitude)")
        val distanceDifference = HaversineAlgorithm.haversineInMeters(
            targetLatitude, currentLatitude, targetLongitude, currentLongitude
        )
        val distanceScore = normalize(value = distanceDifference, min = 0.1, max = maxDistance.toDouble() + 1)
        // calculate age score
        val minAge = currentUser.userPreferences.ageRange.min
        val maxAge = currentUser.userPreferences.ageRange.max
        val ageScore = normalize(
            value = targetUser.userInformation.age.toDouble(),
            min = minAge.toDouble(),
            max = maxAge.toDouble()
        )
        // calculate score for last login
        val lastLoginTimestamp = targetUser.lastLogin
        val timeDifference = System.currentTimeMillis() - lastLoginTimestamp
        val daysAgo = timeDifference / ONE_DAY_TIMESTAMP
        val activeScore = abs((0.1 * daysAgo) - 1.0)
        // apply weightages
        logger.info("**************************")
        logger.info("distant score: $distanceScore")
        logger.info("age score: $ageScore")
        logger.info("active score: $activeScore")
        logger.info("**************************")
        val finalScore =
            (DISTANCE_WEIGHT * distanceScore) +
                    (AGE_WEIGHT * ageScore) +
                    (ACTIVE_SCORE_WEIGHT * activeScore)

        return targetUser.toRecommendation(
            finalScore = finalScore,
            isBoosted = isBoosted,
            isSuperLiked = isSuperLiked,
            distanceFromUser = distanceDifference.toInt()
        )
    }

    private suspend fun saveUserRecommendations(
        recommendations: PersistableUserRecommendations,
        recommendationsDataSource: UserRecommendationsDataSource,
        update: Boolean
    ) {
        recommendationsDataSource.saveUserRecommendations(recommendations, update)
    }
}