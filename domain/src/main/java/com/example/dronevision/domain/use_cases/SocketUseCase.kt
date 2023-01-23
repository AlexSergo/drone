package com.example.dronevision.domain.use_cases

import com.example.dronevision.domain.repository.SocketDataSource

class SocketUseCase(private val dataSource: SocketDataSource) {

    suspend fun connect() {
        dataSource.connect()
    }

    suspend fun disconnect() {
        dataSource.disconnect()
    }

    suspend fun sendMessage(message: String) {
        dataSource.sendMessage(message)
    }

    suspend fun getMessage(listener: (String) -> Unit) {
        dataSource.getMessage(listener)
    }
}