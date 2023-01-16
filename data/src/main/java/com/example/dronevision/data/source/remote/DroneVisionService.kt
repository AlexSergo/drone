package com.example.dronevision.data.source.remote
import com.example.dronevision.data.source.remote.model.RequestId
import com.example.dronevision.data.source.remote.model.ResponseId
import retrofit2.http.Body
import retrofit2.http.POST

interface DroneVisionService {
    @POST("/api/device/check")
    suspend fun checkRegistration(@Body deviceId: RequestId): ResponseId
}