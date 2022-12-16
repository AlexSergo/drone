package com.example.dronevision.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dronevision.domain.use_cases.DeleteAllUseCase
import com.example.dronevision.domain.use_cases.DeleteTechnicUseCase
import com.example.dronevision.domain.use_cases.GetTechnicsUseCase
import com.example.dronevision.domain.use_cases.SaveTechnicUseCase
import com.example.dronevision.presentation.mapperUI.TechnicMapperUI
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.utils.SpawnTechnic
import com.example.dronevision.utils.SpawnTechnicModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TechnicViewModel() : androidx.lifecycle.ViewModel() {
    fun spawnTechnic(spawnTechnicModel: SpawnTechnicModel) = viewModelScope.launch {
        SpawnTechnic.spawnTechnicLiveData.postValue(spawnTechnicModel)
    }
    
}