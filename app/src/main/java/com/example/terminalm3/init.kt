package com.example.terminalm3

import android.content.Context
import android.net.nsd.NsdServiceInfo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.terminalm3.lan.ipToBroadCast
import com.example.terminalm3.lan.readLocalIP
import com.example.terminalm3.network.BT
import com.example.terminalm3.lan.UDP
import com.example.terminalm3.network.channelNetworkIn
import com.example.terminalm3.network.decoder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.terminalm3.screen.lazy.LineTextAndColor
import com.example.terminalm3.screen.lazy.PairTextAndColor
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

        BT.init(context)
        //BT.getPairedDevices()
        //BT.autoconnect(context)

        shared = context.getSharedPreferences("size", Context.MODE_PRIVATE)
        console.fontSize = (shared.getString("size", "12")?.toInt() ?: 12).sp

        //MARK: Вывод символа энтер
        isCheckUseCRLF = shared.getBoolean("enter", false)

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
        GlobalScope.launch(
            Dispatchers.IO
        ) {
            udp.receiveScope(8888, channelNetworkIn)
        }

        decoder.run()
        decoder.addCmd("pong") {

        }

        val version = 281 //BuildConfig.VERSION_NAME

        //Нужно добавить ее в список лази как текущую
        console.messages.add(
            LineTextAndColor(
                text = "Первый нах", pairList = listOf(
                    PairTextAndColor(
                        text = " RTT ", colorText = Color(0xFFFFAA00), colorBg = Color(0xFF812C12)
                    ), PairTextAndColor(
                        text = " Terminal ",
                        colorText = Color(0xFFC6D501),
                        colorBg = Color(0xFF587C2F)
                    ), PairTextAndColor(
                        text = " $version ",
                        colorText = Color(0xFF00E2FF),
                        colorBg = Color(0xFF334292)
                    ), PairTextAndColor(
                        text = ">", colorText = Color(0), colorBg = Color(0xFFFF0000)
                    ), PairTextAndColor(
                        text = "!", colorText = Color(0), colorBg = Color(0xFFFFCC00)
                    ), PairTextAndColor(
                        text = ">", colorText = Color(0), colorBg = Color(0xFF339900)
                    ), PairTextAndColor(
                        text = ">", colorText = Color(0), colorBg = Color(0xFF0033CC), flash = true
                    )
                )
            )
        )

        console.print("▁", flash = true)

        //console.consoleAdd("") //Пустая строка

        global.ipBroadcast = ipToBroadCast(readLocalIP(context))

    }


}