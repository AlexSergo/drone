package com.example.dronevision.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class RequestId(
    @SerializedName("key")
    val deviceId: String
)
