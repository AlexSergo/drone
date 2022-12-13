package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.Repository
import com.example.dronevision.domain.model.TechnicDTO

class GetTechnicsUseCase(private val repository: Repository) {

    suspend fun execute(): List<TechnicDTO>{
        return repository.getTechnics()
    }
}