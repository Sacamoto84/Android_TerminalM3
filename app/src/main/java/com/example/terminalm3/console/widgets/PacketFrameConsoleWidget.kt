package com.example.terminalm3.console.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.PacketFrame].
 *
 * Request that creates this preview:
 * `ui type=uart-frame title="UART RX" direction=rx channel=UART1 baud=115200 data="AA 55 10 02 01 02 34" fields="0-1|Sync|AA55|Preamble;2|Cmd|10|Command;3|Len|02|Payload length;4-5|Payload|0102|Data;6|CRC|34|Checksum"`
 */
@Composable
fun PacketFrameConsoleWidget(spec: ConsoleWidgetSpec.PacketFrame) {
    val title = spec.title ?: spec.protocol ?: "Packet Frame"
    val meta = buildList {
        spec.protocol
            ?.takeIf { it.isNotBlank() && !it.equals(title, ignoreCase = true) }
            ?.let { protocol -> add(protocol.uppercase()) }
        spec.channel?.takeIf { it.isNotBlank() }?.let(::add)
        spec.baud?.takeIf { it.isNotBlank() }?.let { baud -> add("$baud baud") }
        add("${spec.bytes.size} bytes")
    }.joinToString(" • ")

    FrameCard(
        backgroundColor = spec.backgroundColor,
        borderColor = spec.borderColor
    ) {
        FrameHeader(
            title = title,
            titleColor = spec.titleColor,
            accentColor = spec.accentColor,
            chipText = frameDirectionLabel(spec.direction)
        )

        FrameMetaLine(
            text = meta,
            color = spec.metaColor
        )

        FrameByteGrid(
            bytes = spec.bytes,
            accentColor = spec.accentColor,
            textColor = spec.byteColor
        )

        if (spec.showAscii) {
            FrameAsciiBlock(
                bytes = spec.bytes,
                accentColor = spec.accentColor,
                labelColor = spec.metaColor,
                textColor = spec.byteColor
            )
        }

        if (spec.fields.isNotEmpty()) {
            FrameFieldList(
                fields = spec.fields.map { field ->
                    FrameFieldUi(
                        range = field.range,
                        name = field.name,
                        value = field.value,
                        description = field.description
                    )
                },
                accentColor = spec.accentColor,
                backgroundColor = spec.backgroundColor,
                borderColor = spec.borderColor,
                nameColor = spec.fieldNameColor,
                metaColor = spec.fieldMetaColor,
                descriptionColor = spec.fieldDescriptionColor
            )
        }
    }
}

@Preview(name = "Packet Frame", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewPacketFrameConsoleWidget() {
    WidgetPreviewSurface {
        PacketFrameConsoleWidget(previewPacketFrameSpec())
    }
}
