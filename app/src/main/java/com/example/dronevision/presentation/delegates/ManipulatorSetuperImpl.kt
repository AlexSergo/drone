package com.example.dronevision.presentation.delegates

import android.view.View
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class ManipulatorSetuperImpl: ManipulatorSetuper {
    private fun setupCompass(binding: FragmentOsmdroidBinding, rotationGestureOverlay: RotationGestureOverlay) {
        binding.compassButton.setOnClickListener {
            if (!rotationGestureOverlay.isEnabled) {
                rotationGestureOverlay.isEnabled = true
                binding.lockerView.visibility = View.INVISIBLE
            }
            binding.mapView.mapOrientation = 0.0f
            binding.compassButton.rotation = 0.0f
        }

        binding.compassButton.setOnLongClickListener {
            if (!rotationGestureOverlay.isEnabled) false
            rotationGestureOverlay.setEnabled(false)
            binding.lockerView.visibility = View.VISIBLE
            true
        }
    }

    private fun setupZoomButtons(binding: FragmentOsmdroidBinding) {
        binding.zoomInButton.setOnClickListener { binding.mapView.controller.zoomIn(200) }
        binding.zoomOutButton.setOnClickListener { binding.mapView.controller.zoomOut(200) }
    }

/*    private fun setupDisplayMetrics(binding: FragmentOsmdroidBinding) = binding.run {
        val displayMetrics = resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        scaleBarOverlay.unitsOfMeasure = ScaleBarOverlay.UnitsOfMeasure.metric
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setTextSize(30.0f)
        scaleBarOverlay.setScaleBarOffset(
            displayMetrics.widthPixels / 2,
            displayMetrics.heightPixels - (displayMetrics.density * 70.0f).toInt()
        )
        mapView.overlayManager.add(scaleBarOverlay)
    }*/

    override fun setupManipulators(binding: FragmentOsmdroidBinding, rotationGestureOverlay: RotationGestureOverlay) {
        setupCompass(binding, rotationGestureOverlay)
        setupZoomButtons(binding)
    }
}