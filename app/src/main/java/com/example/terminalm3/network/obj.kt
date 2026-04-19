package com.example.terminalm3.network

import kotlinx.coroutines.channels.Channel

data class NetCommand(
    var cmd: String,
    var newString: Boolean = false,
    var lineId: Long = 0L,
    var channelId: Int = 0
)

//Канал передачи
val channelNetworkIn = Channel<String>(Channel.UNLIMITED)

val channelLastString = Channel<NetCommand>(Channel.UNLIMITED)

val decoder = NetCommandDecoder(channelNetworkIn, channelLastString)
