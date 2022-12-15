package com.example.dronevision.utils

import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.example.dronevision.domain.model.TechnicTypes

object SpawnTechnic {
    val spawnTechnicLiveData: MutableLiveData<SpawnTechnicModel> = MutableLiveData()
}

data class SpawnTechnicModel(
    @DrawableRes val imageRes: Int,
    val type: TechnicTypes
)