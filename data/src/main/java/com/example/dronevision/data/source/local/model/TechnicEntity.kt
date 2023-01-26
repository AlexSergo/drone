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
    val division: String? = null
)