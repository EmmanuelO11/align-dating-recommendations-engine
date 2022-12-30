package com.aligndating.recommendationengine.database

import com.aligndating.recommendationengine.config.DatabaseConfig
import org.litote.kmongo.coroutine.CoroutineDatabase

interface DatabaseFactory {
    fun connect(config: DatabaseConfig): CoroutineDatabase
}