package com.example.dronevision.presentation.ui.targ

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dronevision.domain.use_cases.SocketUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TargetViewModel(private val socketUseCase: SocketUseCase): ViewModel() {

    fun sendMessage(address: String, message: String) = viewModelScope.launch(Dispatchers.IO){
        socketUseCase.connect(address)
        socketUseCase.sendMessage(message)
    }

    fun connect(address: String, port: Int = 8080) = viewModelScope.launch(Dispatchers.IO) {
        socketUseCase.connect(address, port)
    }
}