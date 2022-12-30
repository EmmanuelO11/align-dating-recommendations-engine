package com.aligndating.recommendationengine

import ch.qos.logback.classic.Logger
import com.aligndating.plugins.Logging
import com.aligndating.plugins.configureHTTP
import com.aligndating.plugins.configureMonitoring
import com.aligndating.recommendationengine.config.AppConfig
import com.aligndating.recommendationengine.config.setupConfig
import com.aligndating.recommendationengine.database.DatabaseFactory
import com.aligndating.recommendationengine.di.appModule
import com.aligndating.recommendationengine.extensions.respondError
import com.aligndating.recommendationengine.features.location.source.LocationDataSource
import com.aligndating.recommendationengine.features.recommendation.consumer.RecommendationEngineConsumer
import com.aligndating.recommendationengine.plugins.configureCORS
import com.aligndating.recommendationengine.rabbitmq.RabbitMQConnectionFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import org.koin.core.parameter.parametersOf
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

fun Application.module(testing: Boolean = false, koinModules: List<Module> = listOf(appModule)) {
    install(Koin) {
        slf4jLogger(level = org.koin.core.logger.Level.ERROR)
        modules(koinModules)
    }

    setupConfig()

    val appConfig by inject<AppConfig>()

    // configure logging to TRACE level is not running in production
    if (!appConfig.serverConfig.isProd) {
        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        root.level = ch.qos.logback.classic.Level.TRACE
    }

    // configure services
    val databaseFactory by inject<DatabaseFactory>()
    val databaseConnection = databaseFactory.connect(appConfig.databaseConfig)
    val rabbitMQConnectionFactory by inject<RabbitMQConnectionFactory> { parametersOf(appConfig.rabbitMqConfig) }
    val rabbitMqConnection = rabbitMQConnectionFactory.connect(appConfig.rabbitMqConfig)
    val locationDataSource: LocationDataSource by inject { parametersOf(databaseConnection) }


    // configure features
    install(StatusPages) {
        exception<Throwable> { cause ->
            cause.printStackTrace()
            call.respondError(
                body = cause.localizedMessage
            )
        }
    }

    // configure plugins
    configureHTTP()
    configureMonitoring()
    // install callId feature to associate a callId for every call
    install(CallId) {
        generate(10)
    }
    install(DoubleReceive) // required by the logging feature
    install(Logging) {
        logRequests = true
        logResponses = true
        logFullUrl = true
        logBody = true
        logHeaders = true
        filterPath("/api", "/version", "/openapi")
    }

    configureCORS()
    routing {
        this.get("/") {
            call.respond(HttpStatusCode.OK, "Recommender engine is up and running")
        }
    }
    // start consumers
    launch {
        RecommendationEngineConsumer.startAllConsumers(rabbitMqConnection = rabbitMqConnection)
    }
}