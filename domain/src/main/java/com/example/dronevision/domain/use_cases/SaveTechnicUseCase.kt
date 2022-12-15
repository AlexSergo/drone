package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.repository.TechnicRepository
import com.example.dronevision.domain.model.TechnicDTO

class SaveTechnicUseCase(private val technicRepository: TechnicRepository) {

    suspend fun execute(technicDTO: TechnicDTO){
        technicRepository.saveTechnic(technicDTO)
    }
}