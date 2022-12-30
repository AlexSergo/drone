package com.example.dronevision.presentation.ui.osmdroid_map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dronevision.domain.use_cases.*
import com.example.dronevision.presentation.mapper.SessionStateMapperUi
import com.example.dronevision.presentation.mapper.TechnicMapperUI
import com.example.dronevision.presentation.model.SessionState
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.model.bluetooth.Entity
import com.example.dronevision.utils.FindTarget
import com.example.dronevision.utils.MapType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OsmdroidViewModel(
    private val getTechnicsUseCase: GetTechnicsUseCase,
    private val saveTechnicUseCase: SaveTechnicUseCase,
    private val deleteAllUseCase: DeleteAllUseCase,
    private val deleteTechnicUseCase: DeleteTechnicUseCase,
    private val saveSessionStateUseCase: SaveSessionStateUseCase,
    private val getSessionStateUseCase: GetSessionStateUseCase,
) : ViewModel() {
    
    private val _targetLiveData: MutableLiveData<FindTarget> = MutableLiveData()
    val targetLiveData: MutableLiveData<FindTarget> get() = _targetLiveData
    
    private val _technicListLiveData = MutableLiveData<List<Technic>>()
    val technicListLiveData: LiveData<List<Technic>> get() = _technicListLiveData
    
    private val _sessionStateLiveData = MutableLiveData<SessionState>()
    val sessionStateLiveData: LiveData<SessionState> get() = _sessionStateLiveData
    
    fun getTargetCoordinates(entities: List<Entity>) =
        viewModelScope.launch(Dispatchers.Unconfined) {
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
    
    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        deleteAllUseCase.execute()
    }
    
    fun deleteTechnic(technic: Technic) = viewModelScope.launch(Dispatchers.IO) {
        val technicDTO = TechnicMapperUI.mapTechnicUIToTechnicDTO(technic)
        deleteTechnicUseCase.execute(technicDTO)
    }
    
    fun getSessionState() = viewModelScope.launch(Dispatchers.IO) {
        val sessionStateDto = getSessionStateUseCase.execute()
        if (sessionStateDto != null) {
            val sessionState = SessionStateMapperUi.mapSessionStateDtoToUi(sessionStateDto)
            _sessionStateLiveData.postValue(sessionState)
        } else {
            val sessionState =
                SessionStateMapperUi.mapSessionState(currentMap = MapType.OSM.value, isGrid = false)
            saveSessionState(sessionState)
            _sessionStateLiveData.postValue(sessionState)
        }
    }
    
    fun saveSessionState(sessionState: SessionState) = viewModelScope.launch(Dispatchers.IO) {
        val sessionStateDto = SessionStateMapperUi.mapSessionStateUiToDto(sessionState)
        saveSessionStateUseCase.execute(sessionStateDto)
    }
    
    fun saveGridState(isGrid: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        _sessionStateLiveData.value?.let { sessionState ->
            val sessionStateUpdated = SessionStateMapperUi.mapIsGridState(sessionState, isGrid)
            val sessionStateDto = SessionStateMapperUi.mapSessionStateUiToDto(sessionStateUpdated)
            saveSessionStateUseCase.execute(sessionStateDto)
        }
    }
    
    fun saveCurrentMapState(currentMap: Int) = viewModelScope.launch(Dispatchers.IO) {
        _sessionStateLiveData.value?.let { sessionState ->
            val sessionStateUpdated = SessionStateMapperUi.mapCurrentMapState(sessionState, currentMap)
            val sessionStateDto = SessionStateMapperUi.mapSessionStateUiToDto(sessionStateUpdated)
            saveSessionStateUseCase.execute(sessionStateDto)
        }
    }
}

