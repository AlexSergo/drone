package com.example.dronevision.presentation.delegates

import androidx.fragment.app.FragmentActivity

interface GpsHandler {
    fun checkGPS(fragmentActivity: FragmentActivity): Boolean
}