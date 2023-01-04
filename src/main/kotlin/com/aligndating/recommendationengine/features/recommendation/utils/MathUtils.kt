package com.aligndating.recommendationengine.features.recommendation.utils

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun normalize(value: Double, min: Double, max: Double): Double {
    return 1 - (value - min) / (max - min)
}

object HaversineAlgorithm {

    private const val MEAN_EARTH_RADIUS = 6371.008
    private const val D2R = Math.PI / 180.0

    fun haversineInMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lonDiff = (lon2 - lon1) * D2R
        val latDiff = (lat2 - lat1) * D2R
        val latSin = sin(latDiff / 2.0)
        val lonSin = sin(lonDiff / 2.0)
        val a = latSin * latSin + (cos(lat1 * D2R) * cos(lat2 * D2R) * lonSin * lonSin)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return (MEAN_EARTH_RADIUS * c) * 1000
    }
}