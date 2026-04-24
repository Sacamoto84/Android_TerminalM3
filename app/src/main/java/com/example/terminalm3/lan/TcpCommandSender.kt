package com.example.terminalm3.lan

import com.example.terminalm3.Global
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

object TcpCommandSender {

    suspend fun send(message: String): Result<String> = withContext(Dispatchers.IO) {
        val normalizedMessage = if (message.endsWith('\n')) message else "$message\n"
        val payload = normalizedMessage.toByteArray(Charsets.UTF_8)
        if (payload.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Command is empty"))
        }

        val host = Global.resolvedServerHost()
            ?: return@withContext Result.failure(IllegalStateException("ESP32 host is not set"))

        runCatching {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, Global.tcpCommandPort), 1500)
                socket.keepAlive = true
                socket.tcpNoDelay = true
                socket.soTimeout = 400

                val output = socket.getOutputStream()
                output.write(payload)
                output.flush()
                socket.shutdownOutput()

                val response = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                val input = socket.getInputStream()

                while (true) {
                    try {
                        val count = input.read(buffer)
                        if (count < 0) break
                        if (count == 0) continue
                        response.write(buffer, 0, count)
                    } catch (_: SocketTimeoutException) {
                        break
                    }
                }

                response.toString(Charsets.UTF_8.name()).trim()
            }
        }
    }
}
