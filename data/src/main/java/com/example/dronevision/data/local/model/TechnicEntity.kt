package com.example.dronevision.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technics")
data class TechnicEntity(
    @PrimaryKey
    val id: Int = 0,
    val type: String,
    val x: Float,
    val y: Float
)