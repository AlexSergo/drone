package com.example.dronevision.domain.repository

import com.example.dronevision.domain.model.AuthDto


interface DroneVisionServiceRepository {
    suspend fun getId(authDto: AuthDto): String
}