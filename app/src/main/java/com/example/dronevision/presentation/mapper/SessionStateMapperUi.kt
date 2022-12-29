package com.example.dronevision.presentation.mapper

import com.example.dronevision.domain.model.SessionStateDto
import com.example.dronevision.presentation.model.SessionState

object SessionStateMapperUi {
    fun mapSessionStateUiToDto(sessionState: SessionState): SessionStateDto =
        SessionStateDto(currentMap = sessionState.currentMap, isGrid = sessionState.isGrid)
    
    fun mapSessionStateDtoToUi(sessionStateDto: SessionStateDto): SessionState =
        SessionState(currentMap = sessionStateDto.currentMap, isGrid = sessionStateDto.isGrid)
    
    fun mapSessionState(currentMap: Int, isGrid: Boolean): SessionState =
        SessionState(currentMap = currentMap, isGrid = isGrid)
    
    fun mapIsGridState(sessionState: SessionState, isGrid: Boolean): SessionState =
        sessionState.copy(isGrid = isGrid)
    
    fun mapCurrentMapState(sessionState: SessionState, currentMap: Int): SessionState =
        sessionState.copy(currentMap = currentMap)
}