package com.example.dronevision.presentation.delegates

import android.content.Context
import com.example.dronevision.databinding.FragmentOsmdroidBinding

interface OfflineMapHandler {
    fun offlineMode(binding: FragmentOsmdroidBinding, context: Context)
}