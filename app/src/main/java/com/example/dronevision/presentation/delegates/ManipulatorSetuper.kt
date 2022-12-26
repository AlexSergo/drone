package com.example.dronevision.presentation.delegates

import android.content.Context
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.databinding.FragmentYandexMapBinding
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

interface ManipulatorSetuper {
    fun setupManipulators(binding: FragmentYandexMapBinding)
    fun setupManipulators(binding: FragmentOsmdroidBinding,
                          rotationGestureOverlay: RotationGestureOverlay)
}