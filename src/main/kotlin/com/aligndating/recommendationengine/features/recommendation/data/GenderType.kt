package com.aligndating.recommendationengine.features.recommendation.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GenderType {
    @SerialName("MALE")
    MALE,
    @SerialName("FEMALE")
    FEMALE,
    @SerialName("OTHER")
    OTHER
}