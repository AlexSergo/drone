package com.example.dronevision.presentation.mapper

import com.example.dronevision.domain.model.SessionStateDto
import com.example.dronevision.presentation.model.SessionState

object SessionStateMapperUi {
    fun mapSessionStateUiToDto(sessionState: SessionState): SessionStateDto =
        SessionStateDto(
            currentMap = sessionState.currentMap,
            isGrid = sessionState.isGrid,
            azimuth = sessionState.azimuth,
            latitude = sessionState.latitude,
            longitude = sessionState.longitude,
            plane = sessionState.plane,
            mapOrientation = sessionState.mapOrientation,
            cameraZoomLevel = sessionState.cameraZoomLevel
        )
    
    fun mapSessionStateDtoToUi(sessionStateDto: SessionStateDto): SessionState =
        SessionState(
            currentMap = sessionStateDto.currentMap,
            isGrid = sessionStateDto.isGrid,
            azimuth = sessionStateDto.azimuth,
            latitude = sessionStateDto.latitude,
            longitude = sessionStateDto.longitude,
            plane = sessionStateDto.plane,
            mapOrientation = sessionStateDto.mapOrientation,
            cameraZoomLevel = sessionStateDto.cameraZoomLevel
        )
    
    fun mapSessionState(
        currentMap: Int,
        isGrid: Boolean,
        azimuth: String,
        latitude: Double,
        longitude: Double,
        plane: String,
        mapOrientation: Float,
        cameraZoomLevel: Double
    ): SessionState =
        SessionState(
            currentMap = currentMap,
            isGrid = isGrid,
            azimuth = azimuth,
            latitude = latitude,
            longitude = longitude,
            plane = plane,
            mapOrientation = mapOrientation,
            cameraZoomLevel = cameraZoomLevel
        )
    
    fun mapIsGridState(sessionState: SessionState, isGrid: Boolean): SessionState =
        sessionState.copy(isGrid = isGrid)
    
    fun mapCurrentMapState(sessionState: SessionState, currentMap: Int): SessionState =
        sessionState.copy(currentMap = currentMap)
    
    fun mapSessionStateOnSaveInstance(
        azimuth: String,
        plane: String,
        latitude: Double,
        longitude: Double,
        mapOrientation: Float,
        cameraZoomLevel: Double,
        oldSessionState: SessionState
    ): SessionState =
        oldSessionState.copy(
            azimuth = azimuth,
            plane = plane,
            latitude = latitude,
            longitude = longitude,
            mapOrientation = mapOrientation,
            cameraZoomLevel = cameraZoomLevel,
        )
}