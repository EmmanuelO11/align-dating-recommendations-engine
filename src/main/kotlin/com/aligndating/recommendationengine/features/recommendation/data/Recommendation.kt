package com.aligndating.recommendationengine.features.recommendation.data

import com.aligndating.recommendationengine.features.accounts.data.UserInformation
import org.bson.codecs.pojo.annotations.BsonId


data class RecommendationHolder(
    val recommendation: Recommendation,
    val isBoosted: Boolean,
    val isSuperLiked: Boolean
)

@kotlinx.serialization.Serializable
data class Recommendation(
    val userId: String,
    val user: UserInformation,
    var score: Double,
    val distanceFromUser: Int
)

data class PersistableUserRecommendations(
    @BsonId
    val userId: String,
    val recommendations: List<Recommendation>
)