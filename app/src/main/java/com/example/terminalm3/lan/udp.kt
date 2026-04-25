package com.example.terminalm3.lan

import android.os.StrictMode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import kotlin.coroutines.coroutineContext

class UDP {
    /**
     * Прием строк из UDP порта и помещение в канал
     */
    suspend fun receiveScope(port: Int = 8888, channel: Channel<String>) {
        try {
            Timber.i("Запуск UDP receiveScope")
            val buffer = ByteArray(1024 * 1024)
            DatagramSocket(port).use { socket ->
                socket.broadcast = true
                socket.receiveBufferSize = 1024 * 1024
                socket.soTimeout = 1000
                val packet = DatagramPacket(buffer, buffer.size)
                while (coroutineContext.isActive) {
                    try {
                        packet.length = buffer.size
                        socket.receive(packet)
                        val string = String(
                            packet.data,
                            packet.offset,
                            packet.length,
                            Charsets.UTF_8
                        ) //
                        Timber.v("!UDPRoutine! packet RAW=[%s", string)
                        channel.send(string)
                    } catch (_: SocketTimeoutException) {
                    }
                }
            }
        } catch (e: IOException) {
            if (coroutineContext.isActive) {
                Timber.e(e, "UDP receiveScope остановлен")
            }
        }
    }


    //=====================================================
    // Отправить Udp сообщение * Возвращает OK или ошибку
    // region // sendUDP(messageStr: String, ip :String, port: Int): String

    fun sendUDP(messageStr: String, ip: String, port: Int): String {
        // Hack Prevent crash (sending should be done using an async task)
        val oldPolicy = StrictMode.getThreadPolicy()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            DatagramSocket().use { socket ->
                socket.broadcast = true
                val sendData = messageStr.toByteArray()
                val sendPacket =
                    DatagramPacket(sendData, sendData.size, InetAddress.getByName(ip), port)
                socket.send(sendPacket)
                Timber.d("sendUDP: %s:%d", ip, port)
            }
        } catch (e: IOException) {
            Timber.e("IOException: " + e.message)
            return e.message.toString()
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }
        return "OK"
    }

}
