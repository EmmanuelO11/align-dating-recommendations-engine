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

interface RecommendationEngine {

    suspend fun computeForUsersInArea(
        lat: Double,
        lng: Double,
        excludeUsers: List<String>
    )

    suspend fun computeAndSave(
        userId: String,
        update: Boolean
    )
    suspend fun getUserLocationAndComputeInArea(userId: String)
}