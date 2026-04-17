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
import com.example.terminalm3.lan.UDP
import com.example.terminalm3.lan.ipToBroadCast
import com.example.terminalm3.lan.readLocalIP
import com.example.terminalm3.network.channelNetworkIn
import com.example.terminalm3.network.decoder
import com.example.terminalm3.console.LineTextAndColor
import com.example.terminalm3.console.PairTextAndColor
import com.example.terminalm3.console.ConsoleWidgetProtocol
import com.example.terminalm3.console.emitConsoleWidgetNetworkDemo
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

        // Declare NsdHelper object for service discovery
        nsdHelper = object : NsdHelper(context) {
            override fun onNsdServiceResolved(service: NsdServiceInfo) { // A new network service is available
                // Put your custom logic here!!!
            }

            override fun onNsdServiceLost(service: NsdServiceInfo) { // A network service is no longer available
                // Put your custom logic here!!!
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

        //MARK: Вывод символа энтер
        Global.isCheckUseCRLF = shared.getBoolean("enter", false)

        //MARK: Вывод номера строки
        console.lineVisible = shared.getBoolean("lineVisible", true)

        console.fontSize = shared.getInt("fontSize", 18).sp

        // Initialize DNS-SD service discovery
        nsdHelper?.initializeNsd()

        // Start looking for available audio channels in the network
        nsdHelper?.discoverServices()

        ipAddress = readLocalIP(context)
        Timber.i(ipAddress)

        val udp = UDP()
        GlobalScope.launch(Dispatchers.IO) { udp.receiveScope(8888, channelNetworkIn) }

        decoder.run()

        decoder.addCmd("beep") { _, lineId ->
            CoroutineScope(Dispatchers.Main).launch {
                PhoneBeeper.beep()
                Timber.i("Команда beep")
                //console.printLocalAfterRemoteLine(lineId, "!!!", flash = true)

                console.printComposableAfterRemoteLine(lineId) {
                    Image(
                        painter = painterResource(R.drawable.info),
                        contentDescription = null
                    )
                }





            }



        }

        val widgetCommandHandler: (List<String>, Long) -> Unit = { args, lineId ->
            CoroutineScope(Dispatchers.Main).launch {
                val spec = ConsoleWidgetProtocol.parse(args).getOrElse { error ->
                    Timber.w(error, "Не удалось распарсить команду UI")
                    console.printLocalAfterRemoteLine(
                        remoteLineId = lineId,
                        text = "UI command error: ${error.message}",
                        color = Color(0xFFFF8A80)
                    )
                    return@launch
                }

                console.printWidgetAfterRemoteLine(lineId, spec)
            }
        }

        decoder.addCmd("ui", widgetCommandHandler)
        decoder.addCmd("widget", widgetCommandHandler)
        decoder.addCmd("demo-widgets") { _, lineId ->
            CoroutineScope(Dispatchers.Main).launch {
                console.printLocalAfterRemoteLine(
                    remoteLineId = lineId,
                    text = "Запускаю демо всех виджетов...",
                    color = Color(0xFF80CBC4)
                )
            }

            CoroutineScope(Dispatchers.Main).launch {
                emitConsoleWidgetNetworkDemo(channelNetworkIn)
            }
        }

        val version = 301 //BuildConfig.VERSION_NAME

        //Нужно добавить ее в список лази как текущую
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

        console.messages.add(
            LineTextAndColor(
                text = "Первый нах",
                pairList = pairList
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
