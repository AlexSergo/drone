package com.example.dronevision.data.mapper

import com.example.dronevision.data.source.local.model.SubscriberEntity
import com.example.dronevision.domain.model.SubscriberDTO

object SubscribersMapper {

    fun mapSubscriberDTOToEntity(subscriberDTO: SubscriberDTO): SubscriberEntity{
        return SubscriberEntity(deviceId = subscriberDTO.id, name = subscriberDTO.name, IP = subscriberDTO.IP)
    }

    fun mapSubscriberEntityToDTO(subscriberEntities: List<SubscriberEntity>): List<SubscriberDTO>{
        val result = mutableListOf<SubscriberDTO>()
        subscriberEntities.forEach {
            result.add(SubscriberDTO(id = it.deviceId, name = it.name, IP = it.IP))
        }
        return result
    }
}