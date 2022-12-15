package com.example.dronevision.domain.model

data class TechnicDTO(
    val id: Int,
    val type: TechnicTypes,
    val coords: Coordinates
)