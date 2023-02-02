package com.example.dronevision.domain.model

data class Coordinates(
    val x: Double,
    val y: Double,
    val z: Double = 0.0
): java.io.Serializable