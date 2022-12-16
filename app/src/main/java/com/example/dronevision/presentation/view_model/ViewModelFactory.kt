package com.example.dronevision.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.domain.use_cases.DeleteAllUseCase
import com.example.dronevision.domain.use_cases.DeleteTechnicUseCase
import com.example.dronevision.domain.use_cases.GetTechnicsUseCase
import com.example.dronevision.domain.use_cases.SaveTechnicUseCase

class ViewModelFactory() : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TechnicViewModel::class.java))
            return TechnicViewModel() as T
        throw IllegalAccessException("ViewModel not found!")
    }
}