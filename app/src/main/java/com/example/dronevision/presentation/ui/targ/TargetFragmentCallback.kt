package com.example.dronevision.presentation.ui.targ

import com.example.dronevision.presentation.model.Technic

interface TargetFragmentCallback{
    fun onBroadcastButtonClick(destinationId: String, technic: Technic)
    fun deleteTechnic()
}