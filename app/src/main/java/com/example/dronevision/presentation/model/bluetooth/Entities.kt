package com.example.dronevision.presentation.model.bluetooth

import com.google.gson.annotations.SerializedName

data class Entities(
    @SerializedName("Entities")
    val entities: List<Entity>
)