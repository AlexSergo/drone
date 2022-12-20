package com.example.dronevision.presentation.ui.yandex_map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dronevision.domain.use_cases.DeleteAllUseCase
import com.example.dronevision.domain.use_cases.DeleteTechnicUseCase
import com.example.dronevision.domain.use_cases.GetTechnicsUseCase
import com.example.dronevision.domain.use_cases.SaveTechnicUseCase
import com.example.dronevision.presentation.mapperUI.TechnicMapperUI
import com.example.dronevision.presentation.model.Technic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class YandexMapViewModel(
    private val getTechnicsUseCase: GetTechnicsUseCase,
    private val saveTechnicUseCase: SaveTechnicUseCase,
    private val deleteAllUseCase: DeleteAllUseCase,
    private val deleteTechnicUseCase: DeleteTechnicUseCase
) : androidx.lifecycle.ViewModel() {
    
    private val _technicListLiveData = MutableLiveData<List<Technic>>()
    val technicListLiveData: LiveData<List<Technic>> get() = _technicListLiveData
    
    fun getTechnics() = viewModelScope.launch(Dispatchers.IO) {
        _technicListLiveData.postValue(TechnicMapperUI.mapTechnicsDTOToTechnicUI(getTechnicsUseCase.execute()))
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