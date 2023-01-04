package com.aligndating.recommendationengine.features.recommendation.source

import com.aligndating.recommendationengine.features.recommendation.data.PersistableUserRecommendations
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.litote.kmongo.setTo

class UserRecommendationsDataSourceImpl(
    db: CoroutineDatabase
) : UserRecommendationsDataSource {

    private val recommendationsCollection = db.getCollection<PersistableUserRecommendations>()

    override suspend fun saveUserRecommendations(
        recommendations: PersistableUserRecommendations,
        update: Boolean
    ): Boolean {
        if (update) {
            return recommendationsCollection.updateOne(
                PersistableUserRecommendations::userId eq recommendations.userId,
                set(
                    PersistableUserRecommendations::recommendations setTo recommendations.recommendations
                )
            ).wasAcknowledged()
        } else {
            return recommendationsCollection.save(recommendations)?.wasAcknowledged() ?: return false
        }
    }

    override suspend fun getUserRecommendations(userId: String): PersistableUserRecommendations? {
        return recommendationsCollection.findOneById(userId)
    }
}