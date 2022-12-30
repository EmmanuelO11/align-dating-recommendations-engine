package com.aligndating.recommendationengine.features.location.source

import com.aligndating.recommendationengine.features.location.data.UserLocation
import com.mongodb.client.model.geojson.Point

interface LocationDataSource {
    suspend fun getUsersNearby(point: Point, maxDistance: Int): List<UserLocation>
    suspend fun getUserLocation(userId: String): UserLocation?
}