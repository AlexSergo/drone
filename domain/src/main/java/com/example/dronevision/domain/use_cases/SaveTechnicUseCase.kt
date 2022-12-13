package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.Repository
import com.example.dronevision.domain.model.TechnicDTO

class SaveTechnicUseCase(private val repository: Repository) {

    suspend fun execute(technicDTO: TechnicDTO){
        repository.saveTechnic(technicDTO)
    }
}