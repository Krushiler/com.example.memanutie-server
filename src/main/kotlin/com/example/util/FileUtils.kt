package com.example.util

import io.ktor.server.application.*
import io.ktor.server.engine.*
import java.text.SimpleDateFormat
import java.util.*

fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun generateFilename(prefix: String = "", extension: String = ""): String {
    val dateFormat = SimpleDateFormat("yyyy-M-dd-hh-mm-ss")
    val currentTime = dateFormat.format(Date())
    val postfix = getRandomString(6)
    return "$prefix-$currentTime-$postfix.$extension"
}

fun String?.extension(): String = this?.substringAfterLast('.', "") ?: ""

fun Application.getServerUrl(): String {
    for (connector in (environment as ApplicationEngineEnvironment).connectors) {
        return "${connector.type.name.lowercase()}://127.0.0.1:${connector.port}/"
    }

    return ""
}