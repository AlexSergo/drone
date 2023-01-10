package com.example.dronevision.data.source.remote
import com.example.dronevision.data.source.remote.model.RequestId
import com.example.dronevision.data.source.remote.model.ResponseId
import retrofit2.http.Body
import retrofit2.http.GET

interface DroneVisionService {
    @GET("/api/")
    suspend fun getId(@Body androidId: RequestId): ResponseId

}