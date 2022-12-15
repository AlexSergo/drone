package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.repository.TechnicRepository

class DeleteAllUseCase(private val technicRepository: TechnicRepository) {

    suspend fun execute(){
        technicRepository.deleteAll()
    }
}