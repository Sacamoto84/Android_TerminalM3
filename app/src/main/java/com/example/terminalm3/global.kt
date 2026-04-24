package com.example.terminalm3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.terminalm3.console.Console
import kotlinx.coroutines.flow.MutableStateFlow

object Global {

    const val tcpServerPort = 8888
    const val tcpCommandPort = 8900
    const val udpHeartbeatPort = 8888

    var isInitialized = false

    // Показывать в конце строк визуальные метки CR/LF.
    var isCheckUseCRLF by mutableStateOf(false)

    // Сетевые адреса и режимы подключения.
    var ipBroadcast = "0.0.0.0"
    var ipESP by mutableStateOf("0.0.0.0")
    var isESPmDNSFinding by mutableStateOf(false)

    // true -> берем IP сервера из найденного esp.local, false -> используем ручной IP.
    var isServerIpAuto by mutableStateOf(true)
    var manualServerIp by mutableStateOf("")

    // Состояние основного TCP-подключения к ESP32.
    val tcpConnectionState = MutableStateFlow(TcpConnectionUiState())

    fun normalizedEspHost(): String = normalizeHost(ipESP.removePrefix("/"))

    fun resolvedServerHost(): String? {
        val raw = if (isServerIpAuto) normalizedEspHost() else normalizeHost(manualServerIp)
        return raw.takeUnless { it.isBlank() || it == "0.0.0.0" }
    }

    fun portalUrl(): String {
        val host = normalizedEspHost().ifBlank { "0.0.0.0" }
        return "http://$host"
    }

    private fun normalizeHost(raw: String): String {
        return raw
            .trim()
            .removePrefix("http://")
            .removePrefix("https://")
            .removePrefix("/")
            .substringBefore('/')
            .substringBefore(':')
    }
}

enum class TcpConnectionStage {
    Idle,
    WaitingAddress,
    Connecting,
    Connected,
    Error
}

data class TcpConnectionUiState(
    val stage: TcpConnectionStage = TcpConnectionStage.Idle,
    val host: String = "",
    val port: Int = Global.tcpServerPort,
    val detail: String = ""
)

val console = Console()
