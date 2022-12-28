package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.repository.SessionStateRepository

class GetSessionStateUseCase(private val sessionStateRepository: SessionStateRepository) {
    suspend fun execute() = sessionStateRepository.getSessionState()
}