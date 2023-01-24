package com.example.dronevision.data.repository

import com.example.dronevision.data.mapper.SubscribersMapper
import com.example.dronevision.data.source.LocalDataSource
import com.example.dronevision.domain.model.SubscriberDTO
import com.example.dronevision.domain.repository.SubscribersRepository

class SubscribersRepositoryImpl(private val localDataSource: LocalDataSource): SubscribersRepository {

    override suspend fun getSubscribers(): List<SubscriberDTO> {
        val subs = localDataSource.getSubscribers()
        return SubscribersMapper.mapSubscriberEntityToDTO(subs)
    }

    override suspend fun saveSubscriber(subscriberDTO: SubscriberDTO) {
        val sub = SubscribersMapper.mapSubscriberDTOToEntity(subscriberDTO)
       localDataSource.saveSubscriber(sub)
    }

    override fun removeSubscriber(subscriber: SubscriberDTO) {
        localDataSource.removeSubscriber(SubscribersMapper.mapSubscriberDTOToEntity(subscriber))
    }
}