package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.model.TechnicEntity
import com.example.dronevision.domain.Repository
import com.example.dronevision.domain.model.Technic

class DeleteTechnicUseCase(private val repository: Repository) {

    suspend fun execute(technic: Technic){
        repository.deleteTechnic(
            TechnicEntity(
                type = technic.type.name,
                x = technic.coords.x,
                y = technic.coords.y
            )
        )
    }
}