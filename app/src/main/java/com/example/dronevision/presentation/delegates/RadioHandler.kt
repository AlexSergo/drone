package com.example.dronevision.presentation.delegates

import java.nio.charset.Charset

object RadioHandler{

    private val SEND_COMMAND = "AT+CMGS="

    fun createRadioMessage(issi: String, message: String): String {
        var res = ""
        val ref = "82025305"
        val bytes = message.toByteArray(Charset.forName("ISO-8859-5"))
        val countOfBits = message.length * 2 + ref.length * 2
        val mes = bytes.joinToString("") { "%02x".format(bytes) }
        res = "$SEND_COMMAND$issi,$countOfBits\n$ref$mes"
        return res
    }
}