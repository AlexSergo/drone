package com.example.dronevision.data.source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technics")
data class TechnicEntity(
    @PrimaryKey
    val id: Int,
    val type: String,
    val x: Double,
    val y: Double,
    val h: Double = 0.0,
    val division: String? = null
)