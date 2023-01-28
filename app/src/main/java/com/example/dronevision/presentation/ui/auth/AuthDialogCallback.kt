package com.example.dronevision.presentation.ui.auth

interface AuthDialogCallback {
    fun applyAuth(login: String, password: String)
}