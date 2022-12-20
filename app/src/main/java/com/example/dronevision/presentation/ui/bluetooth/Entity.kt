package com.example.dronevision.presentation.ui.bluetooth

data class Entity(
    val lat: Double,
    val lon: Double,
    val asim: Double,
    val alt: Double,
    val cam_deflect: Double,
    val cam_angle: Double
)