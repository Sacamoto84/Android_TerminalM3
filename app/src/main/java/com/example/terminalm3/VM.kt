package com.example.terminalm3

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terminalm3.network.channelLastString
import com.example.terminalm3.screen.lazy.PairTextAndColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VM : ViewModel() {

    private var uiChannelStarted = false

    fun launchUIChanelReceive() {
        if (uiChannelStarted) return
        uiChannelStarted = true

        viewModelScope.launch(Dispatchers.Default) {
            receiveUILastString()
        }
    }

    // Создание списка pairTextAndColor из исходного текста
    private fun text_to_paitList(
        txt: String,
        mod: PairTextAndColor? = null
    ): SnapshotStateList<PairTextAndColor> {
        val pair: SnapshotStateList<PairTextAndColor> = mutableStateListOf()

        // замена ESC [ на \u001C — это и будет новый разделитель
        val str = txt.replace("\u001B", "\u001C\u001B")
        val list = str.split("\u001C")

        for (str1 in list) {
            if (str1.isEmpty()) continue

            val p = stringcalculate(str1)
            pair.addAll(p)

            if (mod != null) {
                pair.add(mod)
            }
        }

        return pair
    }

    private suspend fun receiveUILastString() {
        for (s in channelLastString) {
            if (s.cmd.isEmpty()) continue

            val mod =
                if (Global.isCheckUseCRLF && !s.newString) {
                    PairTextAndColor("_", Color.Green, Color.Black, true, flash = true)
                } else {
                    null
                }

            val pair = text_to_paitList(s.cmd, mod)

            withContext(Dispatchers.Main.immediate) {
                if (console.messages.messages.isNotEmpty()) {
                    console.messages.messages.last().text = s.cmd
                    console.messages.messages.last().pairList = pair

                    if (s.newString) {
                        console.print("▁", flash = true)
                    }
                    console.recompose()
                }
            }
        }
    }
}
