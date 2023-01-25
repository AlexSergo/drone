package com.example.dronevision.presentation.delegates

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class ManipulatorSetuperImpl: ManipulatorSetuper {
    private fun setupCompass(binding: FragmentOsmdroidBinding, rotationGestureOverlay: RotationGestureOverlay) {
        binding.lockerView.isGone = true
        binding.compassButton.setOnClickListener {
            binding.mapView.mapOrientation = 0.0f
            binding.compassButton.rotation = 0.0f
        }

        binding.compassButton.setOnLongClickListener {
            val isRotationEnabled = rotationGestureOverlay.isEnabled
            rotationGestureOverlay.isEnabled = !isRotationEnabled
            binding.lockerView.isGone = !isRotationEnabled
            true
        }
    }
    
    private fun setupZoomButtons(binding: FragmentOsmdroidBinding) {
        binding.zoomInButton.setOnClickListener { binding.mapView.controller.zoomIn(200) }
        binding.zoomOutButton.setOnClickListener { binding.mapView.controller.zoomOut(200) }
    }
    
    private fun setupInfoCardClickListener(binding: FragmentOsmdroidBinding) {
        binding.infoCard.setOnClickListener {
            val isInfoVisible = binding.linearLayoutInfo.isVisible
            binding.linearLayoutInfo.isVisible = !isInfoVisible
        }
    }

    override fun setupManipulators(binding: FragmentOsmdroidBinding, rotationGestureOverlay: RotationGestureOverlay) {
        setupCompass(binding, rotationGestureOverlay)
        setupZoomButtons(binding)
        setupInfoCardClickListener(binding)
    }
}