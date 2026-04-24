package com.example.terminalm3.lan

import android.os.SystemClock
import com.example.terminalm3.Global
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import kotlin.coroutines.coroutineContext

object UdpHeartbeatClient {

    private const val pingIntervalMs = 2000L
    private const val receiveTimeoutMs = 800
    private const val pingPrefix = "tm3 hb ping seq="
    private const val pongPrefix = "tm3 hb pong seq="

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var nextSequence = 1L

    private var loopJob: Job? = null

    fun start() {
        if (loopJob?.isActive == true) return

        loopJob = scope.launch {
            runLoop()
        }
    }

    private suspend fun runLoop() {
        while (coroutineContext.isActive) {
            val host = Global.resolvedServerHost()
            if (host == null) {
                delay(1000)
                continue
            }

            try {
                runHeartbeatSession(host)
            } catch (error: Throwable) {
                Timber.v(error, "Heartbeat session stopped for %s", host)
                delay(500)
            }
        }
    }

    private suspend fun runHeartbeatSession(host: String) {
        val address = InetAddress.getByName(host)

        DatagramSocket().use { socket ->
            socket.soTimeout = receiveTimeoutMs
            socket.connect(address, Global.udpHeartbeatPort)

            val receiveBuffer = ByteArray(128)

            while (coroutineContext.isActive && Global.resolvedServerHost() == host) {
                val sequence = nextSequence++
                val payload = "$pingPrefix$sequence".toByteArray(Charsets.UTF_8)
                val sentAt = SystemClock.elapsedRealtime()

                socket.send(DatagramPacket(payload, payload.size, address, Global.udpHeartbeatPort))

                try {
                    val responsePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
                    socket.receive(responsePacket)

                    val responseText = String(
                        responsePacket.data,
                        responsePacket.offset,
                        responsePacket.length,
                        Charsets.UTF_8
                    )

                    if (responseText == "$pongPrefix$sequence") {
                        val rttMs = (SystemClock.elapsedRealtime() - sentAt).coerceAtLeast(0L)
                        TcpBridgeClient.noteHeartbeatPong(
                            senderHost = host,
                            rttMs = rttMs
                        )
                    } else {
                        Timber.v("Ignored heartbeat packet: %s", responseText)
                    }
                } catch (_: SocketTimeoutException) {
                    // No pong in this cycle. TcpBridgeClient will decide when timeout becomes critical.
                }

                delay(pingIntervalMs)
            }
        }
    }
}
