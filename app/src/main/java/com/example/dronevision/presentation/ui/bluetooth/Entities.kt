package com.example.dronevision.presentation.ui.bluetooth

import com.google.gson.annotations.SerializedName

data class Entities(
    @SerializedName("Entities")
    val entities: List<Entity>
)