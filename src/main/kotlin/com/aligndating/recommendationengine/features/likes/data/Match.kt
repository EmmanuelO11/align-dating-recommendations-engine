package com.aligndating.recommendationengine.features.likes.data

import org.bson.codecs.pojo.annotations.BsonId

data class Match(
    @BsonId
    val hash: String,
    val userA: String,
    val userB: String,
    val timestamp: Long
)