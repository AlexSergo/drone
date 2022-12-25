package com.example.dronevision.presentation.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.bluetooth.Entity
import com.example.dronevision.utils.CalculateTargetCoordinates
import com.example.dronevision.utils.FindTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TargetViewModel: ViewModel() {

    private val _targetLiveData: MutableLiveData<FindTarget> = MutableLiveData()
    val targetLiveData: MutableLiveData<FindTarget> get() = _targetLiveData

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
}