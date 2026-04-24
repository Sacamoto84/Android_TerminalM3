package com.example.terminalm3.lan

import android.os.SystemClock
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

    private const val heartbeatTimeoutMs = 3000L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var activeSocket: Socket? = null

    @Volatile
    private var reconnectRequested = false

    @Volatile
    private var connectedHost: String = ""

    @Volatile
    private var heartbeatHost: String = ""

    @Volatile
    private var lastHeartbeatPongAt: Long = 0L

    @Volatile
    private var lastHeartbeatRttMs: Long = -1L

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

    fun noteHeartbeatPong(senderHost: String?, rttMs: Long) {
        val host = senderHost?.trim().orEmpty()
        if (host.isBlank()) return

        val expectedHost = connectedHost.ifBlank { Global.resolvedServerHost().orEmpty() }
        if (expectedHost.isBlank() || expectedHost != host) return

        heartbeatHost = host
        lastHeartbeatPongAt = SystemClock.elapsedRealtime()
        lastHeartbeatRttMs = rttMs.coerceAtLeast(0L)

        if (Global.tcpConnectionState.value.stage == TcpConnectionStage.Connected) {
            updateState(
                stage = TcpConnectionStage.Connected,
                host = expectedHost,
                detail = buildConnectedDetail(expectedHost)
            )
        }
    }

    private suspend fun runLoop(channel: Channel<String>) {
        updateState(TcpConnectionStage.Idle, detail = "TCP client stopped")

        while (coroutineContext.isActive) {
            val host = Global.resolvedServerHost()
            if (host == null) {
                clearHeartbeat()
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
            primeHeartbeat(host)

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

                updateState(
                    stage = TcpConnectionStage.Connected,
                    host = host,
                    detail = buildConnectedDetail(host)
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

                        channel.send(String(buffer, 0, count, Charsets.UTF_8))
                    } catch (_: SocketTimeoutException) {
                        if (isHeartbeatStale(host)) {
                            throw ServerStaleException(buildHeartbeatTimeoutMessage(host))
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
                clearHeartbeat()
            }
        }
    }

    private fun primeHeartbeat(host: String) {
        val now = SystemClock.elapsedRealtime()
        val shouldReset = heartbeatHost != host ||
            lastHeartbeatPongAt <= 0L ||
            now - lastHeartbeatPongAt > heartbeatTimeoutMs

        heartbeatHost = host
        if (shouldReset) {
            lastHeartbeatPongAt = now
            lastHeartbeatRttMs = -1L
        }
    }

    private fun clearHeartbeat() {
        heartbeatHost = ""
        lastHeartbeatPongAt = 0L
        lastHeartbeatRttMs = -1L
    }

    private fun isHeartbeatStale(host: String): Boolean {
        if (heartbeatHost != host || lastHeartbeatPongAt <= 0L) return false
        return SystemClock.elapsedRealtime() - lastHeartbeatPongAt > heartbeatTimeoutMs
    }

    private fun buildConnectedDetail(host: String): String {
        val heartbeatLabel = if (lastHeartbeatRttMs >= 0L) {
            "heartbeat ${lastHeartbeatRttMs} ms"
        } else {
            "heartbeat wait"
        }
        return "Connected to $host:${Global.tcpServerPort}, $heartbeatLabel"
    }

    private fun buildHeartbeatTimeoutMessage(host: String): String {
        return "Heartbeat timeout from $host for ${heartbeatTimeoutMs / 1000.0}s"
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
            is ServerStaleException -> error.message ?: "Heartbeat timeout"
            is SocketException -> error.message ?: "Socket error"
            else -> error.message ?: error::class.java.simpleName
        }
    }

    private class ServerStaleException(message: String) : IOException(message)
}
