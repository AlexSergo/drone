package com.example.dronevision.presentation.delegates

import android.content.Context

interface DivisionHandler {
    fun getDivision(context: Context): String?
    fun checkDivision(context: Context): Boolean
}