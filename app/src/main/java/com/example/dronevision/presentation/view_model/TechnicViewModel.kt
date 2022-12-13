package com.example.dronevision.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dronevision.domain.Repository
import com.example.dronevision.domain.use_cases.DeleteAllUseCase
import com.example.dronevision.domain.use_cases.DeleteTechnicUseCase
import com.example.dronevision.domain.use_cases.GetTechnicsUseCase
import com.example.dronevision.domain.use_cases.SaveTechnicUseCase
import com.example.dronevision.presentation.mapperUI.TechnicMapperUI
import com.example.dronevision.presentation.model.Technic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TechnicViewModel(repository: Repository): androidx.lifecycle.ViewModel() {

    private val liveData = MutableLiveData<List<Technic>>()

    private val getTechnicsUseCase by lazy(LazyThreadSafetyMode.NONE){
        GetTechnicsUseCase(repository)
    }

    private val saveTechnicUseCase by lazy(LazyThreadSafetyMode.NONE){
        SaveTechnicUseCase(repository)
    }

    private val deleteAllUseCase by lazy(LazyThreadSafetyMode.NONE){
        DeleteAllUseCase(repository)
    }

    private val deleteTechnicUseCase by lazy(LazyThreadSafetyMode.NONE){
        DeleteTechnicUseCase(repository)
    }

    fun getLiveData(): LiveData<List<Technic>>{
        return liveData
    }

    fun getTechnics() = viewModelScope.launch(Dispatchers.IO) {
        liveData.postValue(TechnicMapperUI.mapTechnicsDTOToTechnicUI(getTechnicsUseCase.execute()))
    }

    fun saveTechnic(technic: Technic) = viewModelScope.launch(Dispatchers.IO){
        saveTechnicUseCase.execute(TechnicMapperUI.mapTechnicUIToTechnicDTO(technic))
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO){
        deleteAllUseCase.execute()
    }

    fun deleteTechnic(technic: Technic) = viewModelScope.launch(Dispatchers.IO){
        deleteTechnicUseCase.execute(TechnicMapperUI.mapTechnicUIToTechnicDTO(technic))
    }
}