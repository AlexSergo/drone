package com.example.dronevision.presentation.delegates

import android.content.Context

interface LocationDialogHandler {
    fun showLocationDialog(context: Context, callback: LocationDialogCallback)
}

interface LocationDialogCallback{
    fun focusCamera()
}