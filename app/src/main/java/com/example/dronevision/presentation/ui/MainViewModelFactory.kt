package com.example.dronevision.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.domain.use_cases.*

class MainViewModelFactory(
    private val saveSessionStateUseCase: SaveSessionStateUseCase,
    private val getSessionStateUseCase: GetSessionStateUseCase,
    private val getIdUseCase: GetIdUseCase,
    private val socketUseCase: SocketUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(
                saveSessionStateUseCase = saveSessionStateUseCase,
                getSessionStateUseCase = getSessionStateUseCase,
                getIdUseCase = getIdUseCase,
                socketUseCase = socketUseCase
            ) as T
        throw IllegalAccessException("ViewModel not found!")
    }
}