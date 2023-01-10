package com.example.dronevision.domain.repository


interface DroneVisionServiceRepository {
    suspend fun getId(androidId: String): String
}