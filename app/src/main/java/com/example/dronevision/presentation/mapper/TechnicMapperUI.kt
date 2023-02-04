package com.example.dronevision.presentation.mapper

import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicDTO
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.domain.model.TechnicTypesRu
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.utils.NGeoCalc

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

    fun mapTextToTechnicUI(text: String): Technic {
        val x = text.substring(text.indexOf("X = ") + 4, text.indexOf("Y = ") - 1)
        val y = text.substring(text.indexOf("Y = ") + 4, text.indexOf("Высота:") - 1)
        val z = text.substring(text.indexOf("Высота:") + 8, text.indexOf("Тип") - 1)
        val type = text.substring(text.indexOf("Тип") + 13, text.indexOf("Подразделение") - 1)
        val division = text.substring(text.indexOf("Подразделение") + 15, text.length)
        return Technic(
            coordinates = Coordinates(x = x.toDouble(), y = y.toDouble(), h = z.toDouble()),
            technicTypes = TechnicTypes.valueOf(type),
            division = division
        )
    }

    fun mapTechnicToText(technic: Technic): String{
        val builder = StringBuilder()
        builder.append("Координаты:\n")
        builder.append("X = " + technic.coordinates.x + "\n")
        builder.append("Y = " + technic.coordinates.y + "\n")
        builder.append("Высота: " + technic.coordinates.h + "\n")
        builder.append("Тип техники: " + technic.technicTypes.name + "\n")
        builder.append("Подразделение: " + technic.division + "\n")
        return builder.toString()
    }

    fun mapTechnicToTextForArtgroup(technic: Technic): String{
        val x = doubleArrayOf(0.0)
        val y = doubleArrayOf(0.0)

        NGeoCalc().wgs84ToPlane(
            x, y,
            doubleArrayOf(0.0),
            NGeoCalc.degreesToRadians(technic.coordinates.x),
            NGeoCalc.degreesToRadians(technic.coordinates.y),
            0.0
        )

        val builder = StringBuilder()
        builder.append("Цель 10 " + TechnicTypesRu[technic.technicTypes] + "\n")
        builder.append("X= " + x[0].toInt().toString() + "\n")
        builder.append("Y= " + y[0].toInt().toString() + "\n")
        builder.append("H= " + 0 + "\n")
        builder.append("Фронт= " + 0 + ", Глубина= " + 0)
        return builder.toString()
    }
}