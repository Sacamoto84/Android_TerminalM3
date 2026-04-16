package com.example.terminalm3.network

import com.example.terminalm3.Global
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber

class NetCommandDecoder(
    private val channelIn: Channel<String>,
    private val channelOutNetCommand: Channel<NetCommand>
) {

    fun addCmd(name: String, cb: (List<String>, Long) -> Unit = { _, _ -> }) {
        cmdList.add(CliCommand(name, cb))
    }

    private var lastString: String = ""
    private var currentLineId: Long = 1L

    @OptIn(DelicateCoroutinesApi::class)
    fun run() {
        Timber.i("Запуск декодировщика")
        GlobalScope.launch(Dispatchers.IO) { decodeScope() }
        GlobalScope.launch(Dispatchers.IO) { cliDecoder() }
    }

    private suspend fun decodeScope() {
        val bigStr = StringBuilder()

        while (true) {
            var string = channelIn.receive()

            if (Global.isCheckUseCRLF) {
                string = string.replace("\r", "\u001B[01;39;05;0;49;05;10mCR\u001B[2m")
            }

            if (string.isEmpty()) continue

            bigStr.append(string)

            while (true) {
                val indexN = bigStr.indexOf('\n')

                if (indexN != -1) {
                    val stringDoN = bigStr.substring(0, indexN)
                    bigStr.delete(0, indexN + 1)

                    lastString += stringDoN

                    if (Global.isCheckUseCRLF) {
                        lastString += "\u001B[01;39;05;15;49;05;27mLF\u001B[2m"
                    }

                    val lineId = currentLineId
                    channelOutCommand.send(CommandPacket(lastString, lineId))
                    channelOutNetCommand.send(NetCommand(lastString, true, lineId))

                    lastString = ""
                    currentLineId++
                } else {
                    lastString += bigStr
                    if (lastString.isNotEmpty()) {
                        channelOutNetCommand.send(NetCommand(lastString, false, currentLineId))
                    }
                    bigStr.clear()
                    break
                }
            }
        }
    }

    private val channelOutCommand = Channel<CommandPacket>(1_000_000)

    data class CommandPacket(val raw: String, val lineId: Long)

    data class CliCommand(
        var name: String,
        var cb: (List<String>, Long) -> Unit
    )

    private val cmdList = mutableListOf<CliCommand>()

    private suspend fun cliDecoder() {
        while (true) {
            parse(channelOutCommand.receive())
        }
    }

    private fun parse(packet: CommandPacket) {
        val str = packet.raw
        if (str.isEmpty()) return

        val parts = str.split(' ').toMutableList()
        val name = parts.first()
        parts.removeFirst()
        val arg = parts.filter { it.isNotEmpty() }

        try {
            val command = cmdList.first { it.name == name }
            command.cb.invoke(arg, packet.lineId)
        } catch (e: Exception) {
            Timber.e("CLI отсутствует команда $name")
        }
    }
}
