package com.aligndating.recommendationengine.features.likes.source.dislike

import com.aligndating.recommendationengine.features.likes.data.Dislike

interface DislikeDataSource {
    suspend fun checkIfUserAHasAlreadyDislikedUserB(userA: String, userB: String): Dislike?
}