package com.example.dronevision.domain.repository

interface SocketDataSource {
    suspend fun connect(address: String, port: Int)
    suspend fun disconnect()
    suspend fun sendMessage(message: String)
    suspend fun getMessage(listener: (String) -> Unit)
}