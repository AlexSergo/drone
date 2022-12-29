package com.example.dronevision.data.mapper

import com.example.dronevision.data.source.local.model.SessionStateEntity
import com.example.dronevision.domain.model.SessionStateDto

object SessionStateDtoMapper {
    fun mapSessionStateDtoToEntity(sessionStateDto: SessionStateDto): SessionStateEntity =
        SessionStateEntity(
            id = 1,
            currentMap = sessionStateDto.currentMap,
            isGrid = sessionStateDto.isGrid
        )
    
    fun mapSessionStateEntityToDto(sessionStateEntity: SessionStateEntity): SessionStateDto =
        SessionStateDto(
            currentMap = sessionStateEntity.currentMap,
            isGrid = sessionStateEntity.isGrid
        )
}