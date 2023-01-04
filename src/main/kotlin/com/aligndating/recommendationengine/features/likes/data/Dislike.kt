package com.aligndating.recommendationengine.features.likes.data

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class Dislike(
    @BsonId
    val id: String,
    val dislikedBy: String,
    val dislikedUser: String,
    val timestamp: Long,
    val hash: String
)