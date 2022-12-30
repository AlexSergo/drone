package com.example.dronevision.presentation.ui.bluetooth

import com.example.dronevision.presentation.model.bluetooth.BluetoothListItem

interface BluetoothCallback {
    fun onClick(item: BluetoothListItem)
}