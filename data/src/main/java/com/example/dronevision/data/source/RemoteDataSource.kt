package com.example.dronevision.data.source

import com.example.dronevision.data.source.remote.DroneVisionService
import com.example.dronevision.data.source.remote.model.RequestId
import com.example.dronevision.data.source.remote.model.ResponseId

class RemoteDataSource(private val api: DroneVisionService) {
    suspend fun getId(androidId: RequestId): ResponseId{
        return api.checkRegistration(androidId)
    }
}