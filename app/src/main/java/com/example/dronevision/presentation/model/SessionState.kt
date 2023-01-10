package com.example.dronevision.presentation.model

data class SessionState(
    val currentMap: Int,
    val isGrid: Boolean,
    val azimuth: String,
    val latitude: Double,
    val longitude: Double,
    val plane: String,
    val mapOrientation: Float,
    val cameraZoomLevel: Double
)
