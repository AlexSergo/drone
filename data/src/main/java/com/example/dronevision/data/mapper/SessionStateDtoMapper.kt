package com.example.dronevision.data.mapper

import com.example.dronevision.data.source.local.model.SessionStateEntity
import com.example.dronevision.domain.model.SessionStateDto

object SessionStateDtoMapper {
    fun mapSessionStateDtoToEntity(sessionStateDto: SessionStateDto): SessionStateEntity =
        SessionStateEntity(
            id = 1,
            map = sessionStateDto.map,
            grid = sessionStateDto.grid
        )
    
    fun mapSessionStateEntityToDto(sessionStateEntity: SessionStateEntity): SessionStateDto =
        SessionStateDto(
            map = sessionStateEntity.map,
            grid = sessionStateEntity.grid
        )
}