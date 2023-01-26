package com.example.dronevision.presentation.delegates

import android.app.AlertDialog
import android.content.Context
import com.example.dronevision.utils.Constants
import com.example.dronevision.utils.SharedPreferences

class DivisionHandlerImpl: DivisionHandler {
    override fun getDivision(context: Context): String? {
        val sharedPreferences = SharedPreferences(context)
        return sharedPreferences.getValue(Constants.DIVISION_KEY)
    }

    override fun checkDivision(context: Context): Boolean {
        if (getDivision(context) != null)
            return true
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Укажите свое подразделение!")
            .setMessage("Укажите подразделение в настройках! (Пункт: Мои данные)")
            .setPositiveButton("ОК") { dialog, id ->
                dialog.cancel()
            }
        builder.create()
        builder.show()
        return false
    }
}