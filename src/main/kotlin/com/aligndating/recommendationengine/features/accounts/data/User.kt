package com.aligndating.recommendationengine.features.accounts.data

import com.aligndating.recommendationengine.features.location.data.CurrentLocation
import com.aligndating.recommendationengine.features.recommendation.data.GenderType
import com.aligndating.recommendationengine.features.recommendation.data.Recommendation
import com.aligndating.recommendationengine.features.recommendation.data.RecommendationHolder
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class User(
    @BsonId
    val profileID: String,
    val phoneNumber: String,
    var userInformation: UserInformation,
    var userPreferences: UserPreferences,
    val lastLogin: Long,
    val shootingStars: Long,
    val remainingSuperStars: Int,
    val remainingBoostCount: Int
) {
    fun toRecommendation(
        finalScore: Double,
        isBoosted: Boolean,
        isSuperLiked: Boolean,
        distanceFromUser: Int
    ): RecommendationHolder = RecommendationHolder(
        recommendation = Recommendation(
            userId = profileID,
            user = userInformation,
            score = finalScore,
            distanceFromUser = distanceFromUser
        ),
        isBoosted = isBoosted,
        isSuperLiked = isSuperLiked
    )
}

@Serializable
data class UserInformation(
    var accountType: AccountType,
    val firstName: String,
    var email: String,
    var age: Int,
    var currentLocation: CurrentLocation?,
    var gender: GenderType,
    var birthLocation: BirthLocation,
    var birthTime: String,
    var dob: String,
    var astrologyChart: List<AstrologyChart>,
    var bio: String,
    var occupation: String,
    var height: String?,
    var measuringSystem: Int = 0,
    var profileMedia: List<String>
) {
    fun getSign(): String {
        return astrologyChart.find { it.planetName == "Sun" }!!.sign
    }
}

enum class AccountType(val value: Int) {
    BASIC(0),
    PREFERRED(1),
    STAR(2)
}

@Serializable
data class UserPreferences(
    val accountVisibility: Boolean,
    val ageRange: AgeRange,
    val maximumDistance: Int,
    val partnerInterests: List<GenderType>,
    val notificationsPreferences: NotificationsPreferences
)

@Serializable
data class BirthLocation(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class AstrologyChart(
    val house: Int,
    val planetName: String,
    val sign: String,
    val description: String
)

@Serializable
data class AgeRange(
    val max: Int,
    val min: Int
) {
    fun isInBetween(age: Int): Boolean = age in min..max
}

@Serializable
data class NotificationsPreferences(
    val chatNotification: Boolean,
    val likeNotification: Boolean,
    val matchNotification: Boolean,
    val pushNotification: Boolean
)