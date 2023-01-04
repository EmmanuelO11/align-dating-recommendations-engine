package com.aligndating.recommendationengine.features.likes.source.dislike

import com.aligndating.recommendationengine.features.likes.data.Dislike
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class DislikeDataSourceImpl(
    db: CoroutineDatabase
) : DislikeDataSource {

    private val dislikesCollection = db.getCollection<Dislike>()

    override suspend fun checkIfUserAHasAlreadyDislikedUserB(userA: String, userB: String): Dislike? {
        return dislikesCollection.find(and(Dislike::dislikedBy eq userA, Dislike::dislikedUser eq userB)).first()
    }
}