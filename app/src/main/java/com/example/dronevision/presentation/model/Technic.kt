package com.example.dronevision.presentation.model

import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.google.gson.annotations.SerializedName

data class Technic(
    @SerializedName("coordinates")
    val coordinates: Coordinates,
    @SerializedName("id")
    val id: Int = 1,
    @SerializedName("technicTypes")
    val technicTypes: TechnicTypes
): java.io.Serializable