package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.Repository
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.Technic
import com.example.dronevision.domain.model.TechnicTypes

class GetTechnicsUseCase(private val repository: Repository) {

    suspend fun execute(): List<Technic>{
        val technics = repository.getTechnics()
        val result = mutableListOf<Technic>()
        for (technic in technics){
            result.add(
                Technic(
                    type = TechnicTypes.valueOf(technic.type),
                    coords = Coordinates(x = technic.x, y = technic.y))
            )
        }
        return result
    }
}