package com.example.dronevision.presentation.mapper

import com.example.dronevision.data.source.local.model.SubscriberEntity
import com.example.dronevision.domain.model.SubscriberDTO
import com.example.dronevision.presentation.model.Subscriber

object SubscribersMapperUI {

    fun mapSubscriberUItoDTO(subscriber: Subscriber): SubscriberDTO {
        return SubscriberDTO(id = subscriber.id, name = subscriber.name, IP = subscriber.IP)
    }

    fun mapSubscribersDTOtoSubscribersUI(subscriberDTO: List<SubscriberDTO>): List<Subscriber>{
        val result = mutableListOf<Subscriber>()
        subscriberDTO.forEach {
            result.add(Subscriber(id = it.id, name = it.name, IP = it.IP))
        }
        return result
    }

}