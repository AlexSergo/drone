package com.example.dronevision.domain.repository

import com.example.dronevision.domain.model.SessionStateDto

interface SessionStateRepository {
    suspend fun saveSessionState(sessionStateDto: SessionStateDto)
    suspend fun getSessionState(): SessionStateDto?
}