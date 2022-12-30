package com.example.dronevision.presentation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dronevision.domain.use_cases.GetSessionStateUseCase
import com.example.dronevision.domain.use_cases.SaveSessionStateUseCase
import com.example.dronevision.presentation.mapper.SessionStateMapperUi
import com.example.dronevision.presentation.model.SessionState
import com.example.dronevision.utils.MapType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val saveSessionStateUseCase: SaveSessionStateUseCase,
    private val getSessionStateUseCase: GetSessionStateUseCase,
) : ViewModel() {
    private val _sessionStateLiveData = MutableLiveData<SessionState>()
    val sessionStateLiveData: LiveData<SessionState> get() = _sessionStateLiveData
    
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
    
    private fun saveSessionState(sessionState: SessionState) = viewModelScope.launch(Dispatchers.IO) {
        val sessionStateDto = SessionStateMapperUi.mapSessionStateUiToDto(sessionState)
        saveSessionStateUseCase.execute(sessionStateDto)
    }
}