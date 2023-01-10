package com.example.dronevision.data.mapper

import com.example.dronevision.data.source.local.model.SessionStateEntity
import com.example.dronevision.domain.model.SessionStateDto

object SessionStateDtoMapper {
    fun mapSessionStateDtoToEntity(sessionStateDto: SessionStateDto): SessionStateEntity =
        SessionStateEntity(
            id = 1,
            currentMap = sessionStateDto.currentMap,
            isGrid = sessionStateDto.isGrid,
            azimuth = sessionStateDto.azimuth,
            latitude = sessionStateDto.latitude,
            longitude = sessionStateDto.longitude,
            plane = sessionStateDto.plane,
            mapOrientation = sessionStateDto.mapOrientation,
            cameraZoomLevel = sessionStateDto.cameraZoomLevel,
        )
    
    fun mapSessionStateEntityToDto(sessionStateEntity: SessionStateEntity): SessionStateDto =
        SessionStateDto(
            currentMap = sessionStateEntity.currentMap,
            isGrid = sessionStateEntity.isGrid,
            azimuth = sessionStateEntity.azimuth,
            latitude = sessionStateEntity.latitude,
            longitude = sessionStateEntity.longitude,
            plane = sessionStateEntity.plane,
            mapOrientation = sessionStateEntity.mapOrientation,
            cameraZoomLevel = sessionStateEntity.cameraZoomLevel,
        )
}