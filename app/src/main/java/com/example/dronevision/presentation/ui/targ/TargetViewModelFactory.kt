package com.example.dronevision.presentation.ui.targ

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.domain.use_cases.SocketUseCase
import com.example.dronevision.presentation.ui.MainViewModel

class TargetViewModelFactory(private val socketUseCase: SocketUseCase): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TargetViewModel::class.java))
            return TargetViewModel(socketUseCase = socketUseCase) as T
        throw IllegalAccessException("ViewModel not found!")
    }
}