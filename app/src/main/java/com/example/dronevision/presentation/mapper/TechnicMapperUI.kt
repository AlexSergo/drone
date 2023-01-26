package com.example.dronevision.presentation.mapper

import com.example.dronevision.domain.model.TechnicDTO
import com.example.dronevision.presentation.model.Technic

object TechnicMapperUI {

    fun mapTechnicsDTOToTechnicUI(technics: List<TechnicDTO>): List<Technic>{
        val result = mutableListOf<Technic>()
        technics.forEach {
            result.add(Technic(
                technicTypes = it.type,
                coordinates = it.coords,
                division = it.division
            ))
        }
        return result
    }

    fun mapTechnicUIToTechnicDTO(technic: Technic): TechnicDTO{
        if (technic.division != null)
        return TechnicDTO(
            id = technic.id,
            type = technic.technicTypes,
            coords = technic.coordinates,
            division = technic.division
        )
        else
            return TechnicDTO(
                id = technic.id,
                type = technic.technicTypes,
                coords = technic.coordinates)
    }
}