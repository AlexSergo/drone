package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.model.SubscriberDTO
import com.example.dronevision.domain.repository.SubscribersRepository

class GetSubscribersUseCase(private val repository: SubscribersRepository) {

    suspend fun execute(): List<SubscriberDTO>{
        return repository.getSubscribers()
    }
}