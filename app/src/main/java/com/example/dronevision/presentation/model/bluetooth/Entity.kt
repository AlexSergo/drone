package com.example.dronevision.presentation.model.bluetooth


class Entity (
    val lat: Double,
    val lon: Double,
    val asim: Double,
    val alt: Double,
    val cam_deflect: Double = 0.0,
    val cam_angle: Double = 0.0,
    val calc_target: Boolean = false)
