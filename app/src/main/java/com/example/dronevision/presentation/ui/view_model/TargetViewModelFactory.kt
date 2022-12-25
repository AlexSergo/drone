package com.example.dronevision.presentation.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TargetViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TargetViewModel::class.java))
            return TargetViewModel() as T
        throw IllegalAccessException("ViewModel not found!")
    }
}