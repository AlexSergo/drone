package com.example.dronevision.presentation.mapperUI

import com.example.dronevision.domain.model.TechnicDTO
import com.example.dronevision.presentation.model.Technic

object TechnicMapperUI {

    fun mapTechnicsDTOToTechnicUI(technics: List<TechnicDTO>): List<Technic>{
        val result = mutableListOf<Technic>()
        technics.forEach {
            result.add(Technic(
                type = it.type,
                coords = it.coords
            ))
        }
        return result
    }

    fun mapTechnicUIToTechnicDTO(technic: Technic): TechnicDTO{
        return TechnicDTO(
            id = technic.id,
            type = technic.type,
            coords = technic.coords
        )
    }
}