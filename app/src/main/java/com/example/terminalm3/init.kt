package com.example.terminalm3

import android.content.Context
import android.net.nsd.NsdServiceInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.terminalm3.lan.UDP
import com.example.terminalm3.lan.TcpBridgeClient
import com.example.terminalm3.lan.ipToBroadCast
import com.example.terminalm3.lan.readLocalIP
import com.example.terminalm3.network.channelNetworkIn
import com.example.terminalm3.network.decoder
import com.example.terminalm3.console.Console
import com.example.terminalm3.console.LineTextAndColor
import com.example.terminalm3.console.PairTextAndColor
import com.example.terminalm3.console.ConsoleWidgetProtocol
import com.example.terminalm3.console.emitConsoleWidgetNetworkDemo
import com.example.terminalm3.console.printWidgetAt
import com.example.terminalm3.console.printWidgetAfterRemoteLine
import com.example.terminalm3.utils.NsdHelper
import com.example.terminalm3.utils.PhoneBeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class Initialization(private val context: Context) {

    private var nsdHelper: NsdHelper? = null

    init {

        // Объект для DNS-SD discovery сервисов в локальной сети.
        nsdHelper = object : NsdHelper(context) {
            override fun onNsdServiceResolved(service: NsdServiceInfo) {
                if (Global.isServerIpAuto) {
                    TcpBridgeClient.requestReconnect("mDNS resolved ${service.host}")
                }
            }

            override fun onNsdServiceLost(service: NsdServiceInfo) {
                if (Global.isServerIpAuto) {
                    TcpBridgeClient.requestReconnect("mDNS lost ${service.serviceName}")
                }
            }
        }

        init0()

    }


    @OptIn(DelicateCoroutinesApi::class)
    fun init0() {

        Timber.plant(Timber.DebugTree())
        Timber.i("Привет")

        shared = context.getSharedPreferences("size", Context.MODE_PRIVATE)
        console.fontSize = (shared.getString("size", "12")?.toInt() ?: 12).sp

        // Показывать в консоли визуальные метки CR/LF.
        Global.isCheckUseCRLF = shared.getBoolean("enter", false)

        // Показывать номера строк слева в консоли.
        console.lineVisible = shared.getBoolean("lineVisible", true)
        Global.isServerIpAuto = shared.getBoolean("serverAutoIp", true)
        Global.manualServerIp = shared.getString("serverManualIp", "") ?: ""

        console.fontSize = shared.getInt("fontSize", 18).sp

        // Инициализация и запуск поиска сетевых сервисов.
        nsdHelper?.initializeNsd()

        nsdHelper?.discoverServices()

        ipAddress = readLocalIP(context)
        Timber.i(ipAddress)

        val udp = UDP()
        GlobalScope.launch(Dispatchers.IO) { udp.receiveScope(8888, channelNetworkIn) }
        TcpBridgeClient.start(channelNetworkIn)

        decoder.run()

        decoder.addCmd("beep") { _, lineId, channelId ->
            CoroutineScope(Dispatchers.Main).launch {
                PhoneBeeper.beep()
                Timber.i("Команда beep")

                console.printComposableAfterRemoteLine(lineId, channelId = channelId) {
                    Image(
                        painter = painterResource(R.drawable.info),
                        contentDescription = null
                    )
                }





            }



        }

        val widgetCommandHandler: (List<String>, Long, Int) -> Unit = { args, lineId, lineChannelId ->
            CoroutineScope(Dispatchers.Main).launch {
                val spec = ConsoleWidgetProtocol.parse(args).getOrElse { error ->
                    Timber.w(error, "Не удалось распарсить команду UI")
                    console.printLocalAfterRemoteLine(
                        remoteLineId = lineId,
                        text = "UI command error: ${error.message}",
                        color = Color(0xFFFF8A80),
                        channelId = lineChannelId
                    )
                    return@launch
                }

                val widgetChannelId = ConsoleWidgetProtocol.parseConsoleChannel(args) ?: lineChannelId
                val widgetSlotIndex = ConsoleWidgetProtocol.parseConsoleSlot(args)

                if (widgetSlotIndex != null) {
                    console.printWidgetAt(widgetSlotIndex, spec, widgetChannelId)
                } else {
                    console.printWidgetAfterRemoteLine(lineId, spec, widgetChannelId)
                }
            }
        }

        decoder.addCmd("ui", widgetCommandHandler)
        decoder.addCmd("widget", widgetCommandHandler)
        decoder.addCmd("demo-widgets") { _, lineId, channelId ->
            CoroutineScope(Dispatchers.Main).launch {
                console.printLocalAfterRemoteLine(
                    remoteLineId = lineId,
                    text = "Запускаю демо всех виджетов...",
                    color = Color(0xFF80CBC4),
                    channelId = channelId
                )
            }

            CoroutineScope(Dispatchers.Main).launch {
                emitConsoleWidgetNetworkDemo(
                    channel = channelNetworkIn,
                    consoleChannelId = channelId
                )
            }
        }

        val clearTerminalHandler: (List<String>, Long, Int) -> Unit = { args, lineId, lineChannelId ->
            CoroutineScope(Dispatchers.Main).launch {
                val targetArg = args.firstOrNull()?.trim()

                when {
                    targetArg.isNullOrEmpty() -> console.clearChannel(lineChannelId)
                    targetArg.equals("all", ignoreCase = true) || targetArg == "*" -> console.clearAll()
                    else -> {
                        val targetChannel = targetArg.toIntOrNull()
                        if (targetChannel == null || targetChannel !in 0 until Console.CHANNEL_COUNT) {
                            console.printLocalAfterRemoteLine(
                                remoteLineId = lineId,
                                text = "Clear command error: terminal must be 0..3 or all",
                                color = Color(0xFFFF8A80),
                                channelId = lineChannelId
                            )
                            return@launch
                        }

                        console.clearChannel(targetChannel)
                    }
                }
            }
        }

        decoder.addCmd("clear-terminal", clearTerminalHandler)
        decoder.addCmd("clear-term", clearTerminalHandler)
        decoder.addCmd("cls", clearTerminalHandler)

        val version = 301

        // Стартовая служебная строка с названием приложения и версией.
        val pairList = listOf(
            PairTextAndColor(
                text = " RTT ",
                colorText = Color(0xFFFFAA00),
                colorBg = Color(0xFF812C12)
            ),
            PairTextAndColor(
                text = " Terminal ",
                colorText = Color(0xFFC6D501),
                colorBg = Color(0xFF587C2F)
            ),
            PairTextAndColor(
                text = " $version ",
                colorText = Color(0xFF00E2FF),
                colorBg = Color(0xFF334292)
            ),
            PairTextAndColor(text = ">", colorText = Color(0), colorBg = Color(0xFFFF0000)),
            PairTextAndColor(text = "!", colorText = Color(0), colorBg = Color(0xFFFFCC00)),
            PairTextAndColor(text = ">", colorText = Color(0), colorBg = Color(0xFF339900)),
            PairTextAndColor(
                text = ">",
                colorText = Color(0),
                colorBg = Color(0xFF0033CC),
                flash = true
            )
        )

        console.addItem(
            LineTextAndColor(
                text = "Первый нах",
                pairList = pairList,
                channelId = console.defaultOutputChannel
            )
        )

        console.printComposable {
            Image(
                painter = painterResource(R.drawable.log),
                contentDescription = null, modifier = Modifier.size(48.dp)
            )
        }

        console.completeRemoteLine(0, 1)


        Global.ipBroadcast = ipToBroadCast(readLocalIP(context))

    }

}

