package com.aligndating.recommendationengine.features.location.data

import kotlinx.serialization.Serializable

@Serializable
data class UserLocation(
    val userId: String,
    val location: LocationPoint
) {
    fun toCurrentLocations(): CurrentLocation = CurrentLocation(
        latitude = location.coordinates[1],
        longitude = location.coordinates[0]
    )
}

@Serializable
data class CurrentLocation(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class LocationPoint(
    val type: String = "Point",
    val coordinates: Array<Double>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationPoint

        if (type != other.type) return false
        if (!coordinates.contentEquals(other.coordinates)) return false

        return true
    }
    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + coordinates.contentHashCode()
        return result
    }
}