package com.example.dronevision.presentation.mapper

import com.example.dronevision.domain.model.SessionStateDto
import com.example.dronevision.presentation.model.SessionState

object SessionStateMapperUi {
    fun mapSessionStateUiToDto(sessionState: SessionState): SessionStateDto =
        SessionStateDto(
            map = sessionState.map,
            grid = sessionState.grid
        )
    
    fun mapSessionStateDtoToUi(sessionStateDto: SessionStateDto): SessionState =
        SessionState(
            map = sessionStateDto.map,
            grid = sessionStateDto.grid
        )
}