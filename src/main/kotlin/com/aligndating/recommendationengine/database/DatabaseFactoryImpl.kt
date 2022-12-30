package com.aligndating.recommendationengine.database

import com.aligndating.recommendationengine.config.DatabaseConfig
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class DatabaseFactoryImpl : DatabaseFactory {

    override fun connect(config: DatabaseConfig): CoroutineDatabase {
        val client = KMongo.createClient(config.connectionString).coroutine
        return client.getDatabase(config.dbName)
    }
}