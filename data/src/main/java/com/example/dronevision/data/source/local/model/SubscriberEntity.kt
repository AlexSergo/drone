package com.example.dronevision.data.source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscribers")
data class SubscriberEntity(
    @PrimaryKey
    val id: String,
    val name: String
)