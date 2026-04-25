package com.example.terminalm3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terminalm3.console.PairTextAndColor
import com.example.terminalm3.network.NetCommand
import com.example.terminalm3.network.channelLastString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VM : ViewModel() {

    private data class ParsedUiText(
        val pairList: List<PairTextAndColor>,
        val clearChannelId: Int? = null
    )

    private data class ParsedUiCommand(
        val command: NetCommand,
        val parsed: ParsedUiText
    )

    private var uiChannelStarted = false

    fun launchUiChannelReceive() {
        if (uiChannelStarted) return
        uiChannelStarted = true

        viewModelScope.launch(Dispatchers.Default) {
            receiveUILastString()
        }
    }

    private fun textToPairList(
        txt: String,
        channelId: Int
    ): ParsedUiText {
        val pair = mutableListOf<PairTextAndColor>()
        var clearChannelId: Int? = null

        fun appendParsedSegment(segment: String) {
            if (segment.isEmpty()) return

            val parsed = stringcalculate(segment, channelId)
            pair.addAll(parsed.pairList)
            if (clearChannelId == null) {
                clearChannelId = parsed.clearChannelId
            }
        }

        var startIndex = 0
        while (startIndex < txt.length) {
            val escIndex = txt.indexOf(ESC_CHAR, startIndex)
            val endIndex = when {
                escIndex == -1 -> txt.length
                escIndex == startIndex -> {
                    val nextEscIndex = txt.indexOf(ESC_CHAR, startIndex + 1)
                    if (nextEscIndex == -1) txt.length else nextEscIndex
                }
                else -> escIndex
            }

            appendParsedSegment(txt.substring(startIndex, endIndex))
            startIndex = endIndex
        }

        return ParsedUiText(pairList = pair, clearChannelId = clearChannelId)
    }

    private suspend fun receiveUILastString() {
        val batch = ArrayList<ParsedUiCommand>(UI_BATCH_LIMIT)
        val compactBatch = ArrayList<ParsedUiCommand>(UI_BATCH_LIMIT)

        for (command in channelLastString) {
            batch.clear()
            compactBatch.clear()

            addParsedCommand(command, batch)
            while (batch.size < UI_BATCH_LIMIT) {
                val queuedCommand = channelLastString.tryReceive().getOrNull() ?: break
                addParsedCommand(queuedCommand, batch)
            }

            if (batch.isEmpty()) continue
            compactUiBatch(batch, compactBatch)

            withContext(Dispatchers.Main.immediate) {
                compactBatch.forEach { parsedCommand ->
                    applyParsedCommand(parsedCommand)
                }
            }
        }
    }

    private fun addParsedCommand(
        command: NetCommand,
        batch: MutableList<ParsedUiCommand>
    ) {
        if (command.cmd.isEmpty()) return

        batch.add(
            ParsedUiCommand(
                command = command,
                parsed = textToPairList(command.cmd, command.channelId)
            )
        )
    }

    private fun compactUiBatch(
        source: List<ParsedUiCommand>,
        target: MutableList<ParsedUiCommand>
    ) {
        source.forEach { update ->
            val lastIndex = target.lastIndex
            val last = target.lastOrNull()
            val canReplaceLast = last != null &&
                last.command.lineId == update.command.lineId &&
                last.command.channelId == update.command.channelId &&
                last.parsed.clearChannelId == null &&
                (!last.command.newString || update.command.newString) &&
                (update.command.newString || update.parsed.clearChannelId == null)

            if (canReplaceLast) {
                target[lastIndex] = update
            } else {
                target.add(update)
            }
        }
    }

    private fun applyParsedCommand(parsedCommand: ParsedUiCommand) {
        val command = parsedCommand.command
        val parsed = parsedCommand.parsed

        parsed.clearChannelId?.let { console.clearChannel(it) }

        console.updateRemoteLine(
            remoteLineId = command.lineId,
            text = command.cmd,
            pairList = parsed.pairList,
            channelId = command.channelId
        )
        if (command.newString) {
            console.completeRemoteLine(command.lineId, command.lineId + 1, command.channelId)
        }
    }

    private companion object {
        private const val ESC_CHAR = '\u001B'
        private const val UI_BATCH_LIMIT = 128
    }
}
