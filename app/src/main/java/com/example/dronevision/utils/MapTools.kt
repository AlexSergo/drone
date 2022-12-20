package com.example.dronevision.utils

import com.yandex.mapkit.geometry.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object MapTools {
    const val EARTH_RADIUS = 6378140.0
    
    fun angleBetween(a: Point, b: Point): Double {
        val lat1 = Math.toRadians(a.latitude)
        val lng1 = Math.toRadians(a.longitude)
        val lat2 = Math.toRadians(b.latitude)
        val deltaLong = Math.toRadians(b.longitude) - lng1
        return (Math.toDegrees(
            atan2(
                sin(deltaLong) * Math.cos(lat2),
                cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLong)
            )
        ) + 360.0) % 360.0
    }
}