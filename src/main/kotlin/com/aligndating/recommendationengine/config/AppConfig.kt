package com.aligndating.recommendationengine.config

import io.ktor.application.*
import org.koin.ktor.ext.inject

class AppConfig {
    lateinit var serverConfig: ServerConfig
    lateinit var databaseConfig: DatabaseConfig
    lateinit var redisConfig: RedisConfig
    lateinit var rabbitMqConfig: RabbitMqConfig
}

fun Application.setupConfig() {
    val appConfig by inject<AppConfig>()

    // Server config
    val serverObject = environment.config.config("ktor.server")
    val isProd = serverObject.property("isProd").getString().toBoolean()
    appConfig.serverConfig = ServerConfig(isProd)

    // db config
    val dbObject = environment.config.config("ktor.database")
    val connectionString = dbObject.property("connectionString").getString()
    val dbName = dbObject.property("dbName").getString()
    appConfig.databaseConfig = DatabaseConfig(connectionString, dbName)

    // redis config
    val redisConfigObject = environment.config.config("redis")
    val redisUrl = redisConfigObject.property("redisUrl").getString()
    appConfig.redisConfig = RedisConfig(redisUrl)

    // rabbitMq config
    val rabbitMqConfigObject = environment.config.config("rabbitMq")
    val rabbitMqConnectionString = rabbitMqConfigObject.property("connectionString").getString()
    appConfig.rabbitMqConfig = RabbitMqConfig(rabbitMqConnectionString)
}

data class ServerConfig(
    val isProd: Boolean
)

data class DatabaseConfig(
    val connectionString: String,
    val dbName: String
)

data class JwtConfig(
    val domain: String,
    val audience: String,
    val realm: String,
    val issuer: String,
    val secret: String
)

data class S3Config(
    val accessKey: String,
    val secretKey: String,
    val bucketName: String,
    val profilePicturePath: String,
    val mediaStorePath: String
)

data class TwilioConfig(
    val accountSid: String,
    val authToken: String,
    val senderPhoneNumber: String
)

data class BloomBeConfig(
    val authToken: String
)

data class AstroDescriptionsConfig(
    val planetDescriptionsFile: String,
    val houseDescriptionsFile: String
)

data class RedisConfig(
    val redisUrl: String
)

data class DivineApiConfig(
    val apiKey: String
)

data class RabbitMqConfig(
    val connectionString: String
)