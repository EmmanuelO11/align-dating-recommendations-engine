package com.aligndating.recommendationengine.features.recommendation.events

import com.aligndating.recommendationengine.features.recommendation.data.GenderType

data class NewUserAddedToAreaEvent(
    val userId: String,
    val lat: Double,
    val lng: Double
)

data class UserRemovedFromAreaEvent(
    val userId: String,
    val lat: Double,
    val lng: Double
)

data class UserDistancePreferenceChangedEvent(
    val userId: String,
    val newDistancePreference: Int,
    val lat: Double,
    val lng: Double
)

data class UserGenderPreferenceChangedEvent(
    val userId: String,
    val newPreference: List<GenderType>,
    val lat: Double,
    val lng: Double
)

data class UserSuperLikedEvent(
    val userId: String,
    val likedBy: String
)

data class AccountDeletedEvent(
    val userId: String,
    val lat: Double,
    val lng: Double
)