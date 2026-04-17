package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.HexDump].
 *
 * Request that creates this preview:
 * `ui type=hex-dump title="RX Buffer" data="48 65 6C 6C 6F 20 57 6F 72 6C 64" width=8 addr=0x1000 ascii=on`
 */
@Composable
fun HexDumpConsoleWidget(spec: ConsoleWidgetSpec.HexDump) {
    val maxAddress = spec.startAddress + spec.bytes.size
    val addressWidth = maxOf(4, maxAddress.toString(16).length)
    val groupedRows = spec.bytes.chunked(spec.bytesPerRow)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        spec.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                color = spec.titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        val header = buildString {
            append("ADDR".padEnd(addressWidth + 2))
            append(" ")
            repeat(spec.bytesPerRow) { index ->
                append(index.toString(16).uppercase().padStart(2, '0'))
                if (index != spec.bytesPerRow - 1) append(' ')
            }
            if (spec.showAscii) append("  ASCII")
        }

        Text(
            text = header,
            color = spec.addressColor,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )

        groupedRows.forEachIndexed { rowIndex, rowBytes ->
            val address = (spec.startAddress + rowIndex * spec.bytesPerRow)
                .toString(16)
                .uppercase()
                .padStart(addressWidth, '0')

            val hexPart = buildString {
                rowBytes.forEachIndexed { index, byte ->
                    append(byte.toString(16).uppercase().padStart(2, '0'))
                    if (index != rowBytes.lastIndex) append(' ')
                }
                if (rowBytes.size < spec.bytesPerRow) {
                    if (rowBytes.isNotEmpty()) append(' ')
                    repeat(spec.bytesPerRow - rowBytes.size) { emptyIndex ->
                        append("  ")
                        if (emptyIndex != spec.bytesPerRow - rowBytes.size - 1) append(' ')
                    }
                }
            }

            val asciiPart = rowBytes.joinToString("") { byte ->
                byte.toPrintableAscii()
            }

            val line = buildString {
                append(address)
                append(": ")
                append(hexPart)
                if (spec.showAscii) {
                    append("  ")
                    append(asciiPart)
                }
            }

            Text(
                text = line,
                color = spec.byteColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun Int.toPrintableAscii(): String {
    return if (this in 32..126) {
        this.toChar().toString()
    } else {
        "."
    }
}

@Preview(name = "Hex Dump", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewHexDumpConsoleWidget() {
    WidgetPreviewSurface {
        HexDumpConsoleWidget(previewHexDumpSpec())
    }
}
