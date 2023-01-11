package com.example.dronevision.domain.repository

import com.example.dronevision.domain.model.SubscriberDTO

interface SubscribersRepository {
    suspend fun getSubscribers(): List<SubscriberDTO>
    suspend fun saveSubscriber(subscriberDTO: SubscriberDTO)
}