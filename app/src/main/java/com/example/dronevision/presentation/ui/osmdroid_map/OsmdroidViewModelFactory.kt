package com.example.dronevision.presentation.ui.osmdroid_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.domain.use_cases.DeleteAllUseCase
import com.example.dronevision.domain.use_cases.DeleteTechnicUseCase
import com.example.dronevision.domain.use_cases.GetTechnicsUseCase
import com.example.dronevision.domain.use_cases.SaveTechnicUseCase

class OsmdroidViewModelFactory(
    private val getTechnicsUseCase: GetTechnicsUseCase,
    private val saveTechnicUseCase: SaveTechnicUseCase,
    private val deleteAllUseCase: DeleteAllUseCase,
    private val deleteTechnicUseCase: DeleteTechnicUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OsmdroidViewModel::class.java))
            return OsmdroidViewModel(
                getTechnicsUseCase = getTechnicsUseCase,
                saveTechnicUseCase = saveTechnicUseCase,
                deleteAllUseCase = deleteAllUseCase,
                deleteTechnicUseCase = deleteTechnicUseCase
            ) as T
        throw IllegalAccessException("ViewModel not found!")
    }
}