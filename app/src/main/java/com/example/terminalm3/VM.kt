package com.example.terminalm3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terminalm3.network.channelLastString
import com.example.terminalm3.console.PairTextAndColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VM : ViewModel() {

    private data class ParsedUiText(
        val pairList: List<PairTextAndColor>,
        val clearChannelId: Int? = null
    )

    private var uiChannelStarted = false

    fun launchUIChanelReceive() {
        if (uiChannelStarted) return
        uiChannelStarted = true

        viewModelScope.launch(Dispatchers.Default) {
            receiveUILastString()
        }
    }

    private fun text_to_paitList(
        txt: String,
        channelId: Int
    ): ParsedUiText {
        val pair = mutableListOf<PairTextAndColor>()
        var clearChannelId: Int? = null

        val str = txt.replace("\u001B", "\u001C\u001B")
        val list = str.split("\u001C")

        for (str1 in list) {
            if (str1.isEmpty()) continue

            val parsed = stringcalculate(str1, channelId)
            pair.addAll(parsed.pairList)
            if (clearChannelId == null) {
                clearChannelId = parsed.clearChannelId
            }
        }

        return ParsedUiText(pairList = pair, clearChannelId = clearChannelId)
    }

    private suspend fun receiveUILastString() {
        for (s in channelLastString) {
            if (s.cmd.isEmpty()) continue
            val parsed = text_to_paitList(s.cmd, s.channelId)

            withContext(Dispatchers.Main.immediate) {
                parsed.clearChannelId?.let { console.clearChannel(it) }

                console.updateRemoteLine(s.lineId, s.cmd, parsed.pairList, s.channelId)
                if (s.newString) {
                    console.completeRemoteLine(s.lineId, s.lineId + 1, s.channelId)
                }
            }
        }
    }
}
