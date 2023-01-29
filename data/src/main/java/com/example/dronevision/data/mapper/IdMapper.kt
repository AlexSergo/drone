package com.example.dronevision.data.mapper

import com.example.dronevision.data.source.remote.model.RequestId
import com.example.dronevision.domain.model.AuthDto

object IdMapper {
    fun mapAuthDtoToRequest(authDto: AuthDto): RequestId {
        return RequestId(deviceId = authDto.deviceId, password = authDto.password)
    }
}