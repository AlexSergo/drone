package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.repository.TechnicRepository
import com.example.dronevision.domain.model.TechnicDTO

class DeleteTechnicUseCase(private val technicRepository: TechnicRepository) {

    suspend fun execute(technicDTO: TechnicDTO){
        technicRepository.deleteTechnic(technicDTO)
    }
}