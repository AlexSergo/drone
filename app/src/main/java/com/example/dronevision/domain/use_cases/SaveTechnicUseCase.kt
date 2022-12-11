package com.example.dronevision.domain.use_cases

import com.example.dronevision.data.model.TechnicEntity
import com.example.dronevision.domain.Repository
import com.example.dronevision.domain.model.Technic

class SaveTechnicUseCase(private val repository: Repository) {

    suspend fun execute(technic: Technic){
        repository.saveTechnic(
            TechnicEntity(type = technic.type.name, x = technic.coords.x, y = technic.coords.y)
        )
    }
}