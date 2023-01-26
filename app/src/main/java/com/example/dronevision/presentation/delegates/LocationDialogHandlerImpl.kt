package com.example.dronevision.presentation.delegates

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LocationDialogHandlerImpl: LocationDialogHandler {

    override fun showLocationDialog(context: Context, callback: LocationDialogCallback) {
        val locationDialogArray = arrayOf(
            "Запросить у Р-187-П1",
            "Запросить у Android",
            "Снять с карты",
            "Найти Прицел",
            "Моё местоположение",
            "Передать Р-187-П1"
        )

        MaterialAlertDialogBuilder(context)
            .setTitle("Координаты")
            .setItems(locationDialogArray) { dialog, which ->
                when (which) {
                    0 -> {}
                    1 -> {}
                    2 -> {}
                    3 -> { callback.focusCamera() }
                    4 -> { callback.findMyLocation() }
                    5 -> {}
                }
            }
            .show()
    }
}