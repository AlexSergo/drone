package com.example.dronevision.presentation.delegates

import android.content.Context
import android.view.View
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class ManipulatorSetuperImpl: ManipulatorSetuper {
    private fun setupCompass(binding: FragmentYandexMapBinding) {
        binding.compassButton.setOnClickListener {
            val cameraPosition = binding.mapView.map.cameraPosition
            binding.mapView.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom, 0.0f, cameraPosition.tilt
                ), Animation(Animation.Type.SMOOTH, 1.0f), null
            )
        }
    }

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

    private fun setupZoomButtons(binding: FragmentYandexMapBinding) {
        binding.zoomInButton.setOnClickListener {
            val cameraPosition = binding.mapView.map.cameraPosition
            binding.mapView.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom + 1, cameraPosition.azimuth, cameraPosition.tilt
                ), Animation(Animation.Type.SMOOTH, 1.0f), null
            )
        }

        binding.zoomOutButton.setOnClickListener {
            val cameraPosition = binding.mapView.map.cameraPosition
            binding.mapView.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom - 1, cameraPosition.azimuth, cameraPosition.tilt
                ), Animation(Animation.Type.SMOOTH, 1.0f), null
            )
        }
    }

    private fun setupZoomButtons(binding: FragmentOsmdroidBinding) {
        binding.zoomInButton.setOnClickListener { binding.mapView.controller.zoomIn() }
        binding.zoomOutButton.setOnClickListener { binding.mapView.controller.zoomOut() }
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

    override fun setupManipulators(binding: FragmentYandexMapBinding) {
        setupCompass(binding)
        setupZoomButtons(binding)
    }

    override fun setupManipulators(binding: FragmentOsmdroidBinding, rotationGestureOverlay: RotationGestureOverlay) {
        setupCompass(binding, rotationGestureOverlay)
        setupZoomButtons(binding)
    }
}