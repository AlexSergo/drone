package com.example.dronevision.presentation.model

import org.osmdroid.views.overlay.Marker

data class DisruptionModel(
    val aimMarker: Marker?,
    val disruption: Marker?
)
