package com.aligndating.recommendationengine.features.likes.source.like

import com.aligndating.recommendationengine.features.likes.data.Like
import com.aligndating.recommendationengine.features.likes.data.LikeType
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class LikeDataSourceImpl(
    db: CoroutineDatabase
) : LikeDataSource {

    private val likesCollection = db.getCollection<Like>()

    override suspend fun getUserShootingStarLikes(userId: String): List<Like> {
        return likesCollection.find(
            and(
                Like::likedUser eq userId,
                Like::likeType eq LikeType.SHOOTING_STAR
            )
        ).toList()
    }

    override suspend fun removeLike(id: String): Boolean {
        return likesCollection.deleteOne(
            Like::id eq id
        ).deletedCount != 0L
    }

    override suspend fun checkIfUserAHasAlreadyLikedUserB(userA: String, userB: String): Boolean {
        return likesCollection.find(and(Like::likedBy eq userA, Like::likedUser eq userB)).first() != null
    }
}