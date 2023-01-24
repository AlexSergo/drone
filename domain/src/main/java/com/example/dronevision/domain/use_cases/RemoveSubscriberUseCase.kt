package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.model.SubscriberDTO
import com.example.dronevision.domain.repository.SubscribersRepository

class RemoveSubscriberUseCase(private val repository: SubscribersRepository) {
    fun execute(subscriber: SubscriberDTO) {
        repository.removeSubscriber(subscriber)
    }
}