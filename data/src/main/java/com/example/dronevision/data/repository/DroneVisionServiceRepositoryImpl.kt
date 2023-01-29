package com.example.dronevision.data.repository

import com.example.dronevision.data.mapper.IdMapper
import com.example.dronevision.data.source.RemoteDataSource
import com.example.dronevision.domain.model.AuthDto
import com.example.dronevision.domain.repository.DroneVisionServiceRepository

class DroneVisionServiceRepositoryImpl(private val remoteDataSource: RemoteDataSource): DroneVisionServiceRepository {
    override suspend fun getId(authDto: AuthDto): String {
        val requestId = IdMapper.mapAuthDtoToRequest(authDto)
        return remoteDataSource.getId(requestId).data
    }
}