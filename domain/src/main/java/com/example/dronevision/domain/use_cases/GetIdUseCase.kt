package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.repository.DroneVisionServiceRepository

class GetIdUseCase(private val repository: DroneVisionServiceRepository) {

    suspend fun execute(androidId: String): String{
        return repository.getId(androidId)
    }
}