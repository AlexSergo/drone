package com.example.dronevision.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.domain.use_cases.GetSubscribersUseCase
import com.example.dronevision.domain.use_cases.SaveSubscriberUseCase

class SubscriberViewModelFactory(
    private val saveSubscriberUseCase: SaveSubscriberUseCase,
    private val getSubscribersUseCase: GetSubscribersUseCase
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriberViewModel::class.java))
            return SubscriberViewModel(
                saveSubscriberUseCase = saveSubscriberUseCase,
                getSubscribersUseCase = getSubscribersUseCase
            ) as T
        throw IllegalAccessException("ViewModel not found!")
    }
}