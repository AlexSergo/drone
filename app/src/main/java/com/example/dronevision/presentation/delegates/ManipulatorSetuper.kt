package com.example.dronevision.presentation.delegates

import com.example.dronevision.databinding.FragmentOsmdroidBinding
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

interface ManipulatorSetuper {
    fun setupManipulators(
        binding: FragmentOsmdroidBinding,
        rotationGestureOverlay: RotationGestureOverlay
    )
}