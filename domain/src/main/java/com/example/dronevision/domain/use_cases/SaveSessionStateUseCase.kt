package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.model.SessionStateDto
import com.example.dronevision.domain.repository.SessionStateRepository

class SaveSessionStateUseCase(private val sessionStateRepository: SessionStateRepository) {
    suspend fun execute(sessionStateDto: SessionStateDto) {
        sessionStateRepository.saveSessionState(sessionStateDto)
    }
}