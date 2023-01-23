package com.example.dronevision.domain.repository

interface SocketDataSource {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun sendMessage(message: String)
    suspend fun getMessage(listener: (String) -> Unit)
}