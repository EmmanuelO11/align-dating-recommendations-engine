package com.aligndating.plugins

import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureMonitoring() {
    install(CallLogging) {
        callIdMdc("X-Request-ID")
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

}
