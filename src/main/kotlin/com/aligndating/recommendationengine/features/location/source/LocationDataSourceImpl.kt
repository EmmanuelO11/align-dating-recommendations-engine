package com.aligndating.recommendationengine.features.location.source

import com.aligndating.recommendationengine.features.location.data.UserLocation
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.geojson.Point
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class LocationDataSourceImpl(
    private val db: CoroutineDatabase
) : LocationDataSource {

    private suspend fun getUserLocationCollection(): CoroutineCollection<UserLocation> {
        val collection: CoroutineCollection<UserLocation> = db.getCollection()
        collection.ensureIndex(Indexes.geo2dsphere("location"))
        return collection
    }

    override suspend fun getUsersNearby(
        point: Point,
        maxDistance: Int
    ): List<UserLocation> {
        return getUserLocationCollection().find(
            UserLocation::location.near(point, maxDistance = maxDistance.toDouble())
        ).toList()
    }

    override suspend fun getUserLocation(userId: String): UserLocation? {
        val collection = getUserLocationCollection()
        return collection.findOne(UserLocation::userId eq userId)
    }
}