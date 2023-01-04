package com.aligndating.recommendationengine.features.recommendation.source

import com.aligndating.recommendationengine.features.recommendation.data.PersistableUserRecommendations

interface UserRecommendationsDataSource {
    suspend fun saveUserRecommendations(
        recommendations: PersistableUserRecommendations, update: Boolean
    ): Boolean
    suspend fun getUserRecommendations(userId: String): PersistableUserRecommendations?
}