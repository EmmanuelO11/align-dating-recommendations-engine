package com.aligndating.recommendationengine.plugins

import com.aligndating.recommendationengine.extensions.getLogger
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*

fun Application.configureCORS() {

    val logger = getLogger()

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowNonSimpleContentTypes = true
//        allowCredentials = true
        allowSameOrigin = true
        host("*", listOf("http", "https"))
        logger.info("CORS enabled for $hosts")
    }
}