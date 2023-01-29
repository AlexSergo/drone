package com.example.dronevision.presentation.ui.auth

interface AuthDialogCallback {
    fun checkRegistration(id: String, password: String)
}