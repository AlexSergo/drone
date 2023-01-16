package com.example.dronevision.data.mapper

import com.example.dronevision.data.source.remote.model.RequestId

object IdMapper {
    fun mapStringIdToRequestObject(androidId: String): RequestId{
        return RequestId(deviceId = androidId)
    }
}