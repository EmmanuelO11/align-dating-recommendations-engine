package com.aligndating.recommendationengine.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> dbQuery(block: suspend  () -> T?): T? =
    withContext(Dispatchers.IO) {
        block()
    }