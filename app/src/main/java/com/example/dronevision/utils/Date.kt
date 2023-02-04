package com.example.dronevision.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Перевод календаря в строку с определенным форматом.
 *
 * @param format формат ожидаемой даты в строке.
 *
 * @author Voroncov Yuri (voroncoff.yuri@gmail.com)
 */
@SuppressLint("SimpleDateFormat")
fun Calendar.toFormat(format: String): String {
    return SimpleDateFormat(format).format(this.time)
}