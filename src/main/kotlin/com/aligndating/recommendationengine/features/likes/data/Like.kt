package com.aligndating.recommendationengine.features.likes.data

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class Like(
    @BsonId
    val id: String,
    val likedUser: String,
    val likedBy: String,
    val hash: String,
    val likeType: LikeType,
    val timestamp: Long
)

enum class LikeType {
    REGULAR, SHOOTING_STAR
}