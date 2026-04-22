package com.example.terminalm3.lan

import com.example.terminalm3.Global
import com.example.terminalm3.TcpConnectionStage
import com.example.terminalm3.TcpConnectionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.EOFException
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext

object TcpBridgeClient {

    private const val serverStaleTimeoutMs = 10000L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var activeSocket: Socket? = null

    @Volatile
    private var reconnectRequested = false

    @Volatile
    private var connectedHost: String = ""

    @Volatile
    private var lastServerActivityAt: Long = 0L

    @Volatile
    private var lastServerActivitySource: String = ""

    private var loopJob: Job? = null

    fun start(channel: Channel<String>) {
        if (loopJob?.isActive == true) return

        loopJob = scope.launch {
            runLoop(channel)
        }
    }

    fun requestReconnect(reason: String = "") {
        reconnectRequested = true
        if (reason.isNotBlank()) {
            Timber.i("TCP reconnect requested: %s", reason)
        }
        closeActiveSocket()
    }

    fun noteUdpPacket(senderHost: String?) {
        val host = senderHost?.trim().orEmpty()
        if (host.isBlank()) return

        val expectedHost = connectedHost.ifBlank { Global.resolvedServerHost().orEmpty() }
        if (expectedHost.isBlank() || expectedHost != host) return

        touchServerActivity("udp")
    }

    private suspend fun runLoop(channel: Channel<String>) {
        updateState(TcpConnectionStage.Idle, detail = "TCP client stopped")

        while (coroutineContext.isActive) {
            val host = Global.resolvedServerHost()
            if (host == null) {
                clearServerActivity()
                connectedHost = ""
                updateState(
                    stage = TcpConnectionStage.WaitingAddress,
                    detail = if (Global.isServerIpAuto) {
                        "Waiting esp.local address"
                    } else {
                        "Enter server IP"
                    }
                )
                delay(1000)
                continue
            }

            val socket = Socket()
            activeSocket = socket
            reconnectRequested = false
            connectedHost = host
            clearServerActivity()

            try {
                updateState(
                    stage = TcpConnectionStage.Connecting,
                    host = host,
                    detail = "Connecting to $host:${Global.tcpServerPort}"
                )

                socket.connect(InetSocketAddress(host, Global.tcpServerPort), 1500)
                socket.keepAlive = true
                socket.tcpNoDelay = true
                socket.soTimeout = 1200

                touchServerActivity("tcp")
                updateState(
                    stage = TcpConnectionStage.Connected,
                    host = host,
                    detail = "Connected to $host:${Global.tcpServerPort}"
                )

                val buffer = ByteArray(8 * 1024)
                val input = socket.getInputStream()

                while (coroutineContext.isActive && !socket.isClosed) {
                    val currentHost = Global.resolvedServerHost()
                    if (reconnectRequested || currentHost != host) {
                        break
                    }

                    try {
                        val count = input.read(buffer)
                        if (count < 0) throw EOFException("TCP stream closed")
                        if (count == 0) continue

                        touchServerActivity("tcp")
                        channel.send(String(buffer, 0, count, Charsets.UTF_8))
                    } catch (_: SocketTimeoutException) {
                        if (isServerStale()) {
                            throw ServerStaleException(buildStaleMessage())
                        }
                    }
                }

                if (coroutineContext.isActive && !reconnectRequested) {
                    updateState(
                        stage = TcpConnectionStage.Error,
                        host = host,
                        detail = "Connection closed"
                    )
                    delay(800)
                }
            } catch (error: Throwable) {
                if (reconnectRequested) {
                    updateState(
                        stage = TcpConnectionStage.Connecting,
                        host = host,
                        detail = "Reconnecting..."
                    )
                } else {
                    updateState(
                        stage = TcpConnectionStage.Error,
                        host = host,
                        detail = humanReadableError(error)
                    )
                    delay(1200)
                }
            } finally {
                closeActiveSocket()
                reconnectRequested = false
                connectedHost = ""
                clearServerActivity()
            }
        }
    }

    private fun touchServerActivity(source: String) {
        lastServerActivityAt = System.currentTimeMillis()
        lastServerActivitySource = source
    }

    private fun clearServerActivity() {
        lastServerActivityAt = 0L
        lastServerActivitySource = ""
    }

    private fun isServerStale(): Boolean {
        if (lastServerActivityAt <= 0L) return false
        return System.currentTimeMillis() - lastServerActivityAt > serverStaleTimeoutMs
    }

    private fun buildStaleMessage(): String {
        val sourceLabel = if (lastServerActivitySource.isBlank()) {
            "server"
        } else {
            "server via $lastServerActivitySource"
        }
        return "No activity from $sourceLabel for ${serverStaleTimeoutMs / 1000}s"
    }

    private fun closeActiveSocket() {
        try {
            activeSocket?.close()
        } catch (_: Throwable) {
        } finally {
            activeSocket = null
        }
    }

    private fun updateState(
        stage: TcpConnectionStage,
        host: String = "",
        detail: String = ""
    ) {
        Global.tcpConnectionState.value = TcpConnectionUiState(
            stage = stage,
            host = host,
            port = Global.tcpServerPort,
            detail = detail
        )
    }

    private fun humanReadableError(error: Throwable): String {
        return when (error) {
            is UnknownHostException -> "Server host not found"
            is SocketTimeoutException -> "Connection timeout"
            is EOFException -> "Server closed connection"
            is ServerStaleException -> error.message ?: "Server activity timeout"
            is SocketException -> error.message ?: "Socket error"
            else -> error.message ?: error::class.java.simpleName
        }
    }

    private class ServerStaleException(message: String) : IOException(message)
}
