package com.aligndating.recommendationengine.extensions

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.respondError(
    body: Any,
    httpStatusCode: HttpStatusCode = HttpStatusCode.BadRequest
) = respond(httpStatusCode, body)