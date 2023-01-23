package com.example.dronevision.data.source

import com.example.dronevision.domain.repository.SocketDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.notifyAll
import java.net.Socket
import java.util.Scanner

class SocketDataSourceImpl(private val address: String, private val port: Int) : SocketDataSource {

    private lateinit var socket: Socket
    private var isConnected: Boolean = false

    override suspend fun connect(){
        withContext(Dispatchers.IO) {
            socket = Socket(address, port)
            socket.connect(socket.remoteSocketAddress)
            isConnected = true
        }
    }

    override suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            socket.close()
        }
    }

    override suspend fun sendMessage(message: String) {
        if (!isConnected)
            connect()
      withContext(Dispatchers.IO) {
          val writer = socket.getOutputStream()
          if (socket.isConnected)
            writer.write(message.toByteArray())
          else
              writer.close()
      }
    }

    override suspend fun getMessage(listener: (String) -> Unit) {
        if (!isConnected)
            connect()
        withContext(Dispatchers.IO) {
            var data = ""
            val reader = Scanner(socket.getInputStream())
            while (socket.isConnected){
                var input = ""
                input = reader.nextLine()
                if (data.length < 300)
                    data += "\n$input"
                else
                    data = input
                listener.notifyAll()
            }
            reader.close()
            disconnect()
        }
    }
}