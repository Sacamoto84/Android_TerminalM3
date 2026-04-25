package com.example.terminalm3.lan

import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Получить IP адрес Wifi подключения
 *
 * @param ip
 *
 * @return true если данный ресурс доступен
 */
fun ping(ip: String = "http://192.168.0.200"): Boolean {
    var connection: HttpURLConnection? = null
    try {
        val url = URL(ip)
        connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Connection", "close")
        connection.connectTimeout = 1000
        connection.connect()

        return when (connection.responseCode) {
            200, 403 -> true
            else -> false
        }

    } catch (e: Exception) {
        val s = when (e) {
            is MalformedURLException -> "loadLink: Invalid URL ${e.message}"
            is IOException -> "loadLink: IO Exception reading data: ${e.message}"
            is SecurityException -> "loadLink: Security Exception. Needs permission? ${e.message}"

            else -> "Unknown error: ${e.message}"
        }
        Timber.e(e, s)
    } finally {
        connection?.disconnect()
    }
    return false
}