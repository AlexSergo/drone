package com.example.dronevision.presentation.ui.osmdroid_map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dronevision.domain.use_cases.DeleteAllUseCase
import com.example.dronevision.domain.use_cases.DeleteTechnicUseCase
import com.example.dronevision.domain.use_cases.GetTechnicsUseCase
import com.example.dronevision.domain.use_cases.SaveTechnicUseCase
import com.example.dronevision.presentation.mapperUI.TechnicMapperUI
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.bluetooth.Entity
import com.example.dronevision.utils.FindTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OsmdroidViewModel(
    private val getTechnicsUseCase: GetTechnicsUseCase,
    private val saveTechnicUseCase: SaveTechnicUseCase,
    private val deleteAllUseCase: DeleteAllUseCase,
    private val deleteTechnicUseCase: DeleteTechnicUseCase
) : ViewModel() {
    
    private val _targetLiveData: MutableLiveData<FindTarget> = MutableLiveData()
    val targetLiveData: MutableLiveData<FindTarget> get() = _targetLiveData
    
    private val _technicListLiveData = MutableLiveData<List<Technic>>()
    val technicListLiveData: LiveData<List<Technic>> get() = _technicListLiveData
    
    fun getTargetCoordinates(entities: List<Entity>) = viewModelScope.launch(Dispatchers.Unconfined) {
        val drone = entities[0]
        var lat = drone.lat
        var lon = drone.lon
        if (lat.isNaN() && lon.isNaN()) {
            lat = 0.0
            lon = 0.0
        }
        val alt = drone.alt
        val ywr = drone.cam_deflect
        val pt = drone.cam_angle
        val asim = drone.asim
        val findTarget = FindTarget(alt, lat, lon, asim + ywr, pt)
        _targetLiveData.postValue(findTarget)
    }
    
    fun getTechnics() = viewModelScope.launch(Dispatchers.IO) {
        val mapTechnicsDTOToTechnicUI =
            TechnicMapperUI.mapTechnicsDTOToTechnicUI(getTechnicsUseCase.execute())
        _technicListLiveData.postValue(mapTechnicsDTOToTechnicUI)
    }
    
    fun saveTechnic(technic: Technic) = viewModelScope.launch(Dispatchers.IO){
        val technicDTO = TechnicMapperUI.mapTechnicUIToTechnicDTO(technic)
        saveTechnicUseCase.execute(technicDTO)
    }
    
    fun deleteAll() = viewModelScope.launch(Dispatchers.IO){
        deleteAllUseCase.execute()
    }
    
    fun deleteTechnic(technic: Technic) = viewModelScope.launch(Dispatchers.IO){
        val technicDTO = TechnicMapperUI.mapTechnicUIToTechnicDTO(technic)
        deleteTechnicUseCase.execute(technicDTO)
    }
}

