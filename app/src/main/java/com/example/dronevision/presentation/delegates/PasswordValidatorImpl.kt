package com.example.dronevision.presentation.delegates

import android.content.Context
import android.widget.Toast

class PasswordValidatorImpl: PasswordValidator {
    override fun makeToastWrongPassword(context: Context) {
        Toast.makeText(
            context,
            "Неверный пароль",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun makeToastEmptyPassword(context: Context) {
        Toast.makeText(context, "Пароль не может быть пустым", Toast.LENGTH_LONG).show()
    }

    override fun makeToastSmallPassword(context: Context) {
        Toast.makeText(
            context,
            "Пароль не может быть короче 4 символов",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun makeToastLongPassword(context: Context) {
        Toast.makeText(
            context,
            "Пароль не может быть длиннее 10 символов",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun makeToastPasswordsNotMatch(context: Context) {
        Toast.makeText(
            context,
            "Пароль не совпадают",
            Toast.LENGTH_LONG
        ).show()
    }
}