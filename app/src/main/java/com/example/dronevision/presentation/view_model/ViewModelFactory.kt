package com.example.dronevision.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.domain.Repository

class ViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TechnicViewModel::class.java))
            return TechnicViewModel(repository) as T
        throw IllegalAccessException("ViewModel not found!")
    }
}