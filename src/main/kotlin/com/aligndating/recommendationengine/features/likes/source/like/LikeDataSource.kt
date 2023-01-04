package com.aligndating.recommendationengine.features.likes.source.like

import com.aligndating.recommendationengine.features.likes.data.Like


interface LikeDataSource {
    suspend fun checkIfUserAHasAlreadyLikedUserB(userA: String, userB: String): Boolean
    suspend fun getUserShootingStarLikes(userId: String): List<Like>
    suspend fun removeLike(id: String): Boolean
}