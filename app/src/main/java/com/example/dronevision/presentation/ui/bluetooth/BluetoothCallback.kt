package com.example.dronevision.presentation.ui.bluetooth

import com.example.dronevision.presentation.model.BluetoothListItem

interface BluetoothCallback {
    fun onClick(item: BluetoothListItem)
}