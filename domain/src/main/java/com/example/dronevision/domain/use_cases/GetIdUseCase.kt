package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.model.AuthDto
import com.example.dronevision.domain.repository.DroneVisionServiceRepository

class GetIdUseCase(private val repository: DroneVisionServiceRepository) {

    suspend fun execute(authDto: AuthDto): String {
        return repository.getId(authDto)
    }
}