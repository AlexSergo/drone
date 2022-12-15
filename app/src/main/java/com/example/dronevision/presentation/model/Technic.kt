package com.example.dronevision.presentation.model

import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes

data class Technic(
    val id: Int = 0,
    val type: TechnicTypes,
    val coords: Coordinates
)