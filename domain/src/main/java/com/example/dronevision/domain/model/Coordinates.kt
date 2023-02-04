package com.example.dronevision.domain.model

data class Coordinates(
    val x: Double,
    val y: Double,
    val h: Double = 0.0
): java.io.Serializable