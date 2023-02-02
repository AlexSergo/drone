package com.example.dronevision.presentation.delegates

import android.content.Context

interface PasswordValidator {
    fun makeToastWrongPassword(context: Context)
    fun makeToastEmptyPassword(context: Context)
    fun makeToastSmallPassword(context: Context)
    fun makeToastLongPassword(context: Context)
    fun makeToastPasswordsNotMatch(context: Context)
}