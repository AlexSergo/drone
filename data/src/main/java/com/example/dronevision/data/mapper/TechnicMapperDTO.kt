package com.example.dronevision.data.mapper

import com.example.dronevision.data.source.local.model.TechnicEntity
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicDTO
import com.example.dronevision.domain.model.TechnicTypes

object TechnicMapperDTO {
    fun mapTechnicDTOtoEntity(technicDTO: TechnicDTO): TechnicEntity {
        return TechnicEntity(
            id = technicDTO.id,
            type = technicDTO.type.name,
            x = technicDTO.coords.x,
            y = technicDTO.coords.y
        )
    }

    fun mapEntitiesToTechnicsDTO(technics: List<TechnicEntity>): List<TechnicDTO> {
        val result = mutableListOf<TechnicDTO>()
        technics.forEach {
            result.add(TechnicDTO(
                id = it.id,
                type = TechnicTypes.valueOf(it.type),
                coords = Coordinates(x = it.x, y = it.y)
            ))
        }
        return result
    }
}