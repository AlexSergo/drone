package com.example.dronevision.data.repository

import com.example.dronevision.data.mapper.SessionStateDtoMapper
import com.example.dronevision.data.source.LocalDataSource
import com.example.dronevision.domain.model.SessionStateDto
import com.example.dronevision.domain.repository.SessionStateRepository

class SessionStateRepositoryImpl(private val localDataSource: LocalDataSource) : SessionStateRepository {
    
    override suspend fun saveSessionState(sessionStateDto: SessionStateDto) {
        val sessionStateDto = SessionStateDtoMapper.mapSessionStateDtoToEntity(sessionStateDto)
        localDataSource.saveSessionState(sessionStateDto)
    }
    
    override suspend fun getSessionState(): SessionStateDto {
        val sessionStateEntity = localDataSource.getSessionState()
        return SessionStateDtoMapper.mapSessionStateEntityToDto(sessionStateEntity)
    }
}