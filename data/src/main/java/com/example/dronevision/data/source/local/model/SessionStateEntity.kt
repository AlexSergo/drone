package com.example.dronevision.data.source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "state_session")
data class SessionStateEntity(
    @PrimaryKey
    val id: Int,
    val currentMap: Int,
    val isGrid: Boolean,
    val azimuth: String,
    val latitude: Double,
    val longitude: Double,
    val plane: String,
    val mapOrientation: Float,
    val cameraZoomLevel: Double
)