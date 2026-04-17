package com.example.terminalm3.console.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.CanFrame].
 *
 * Request that creates this preview:
 * `ui type=can-frame title="Motor CAN" direction=rx id=0x18FF50E5 ext=true data="11 22 33 44 55 66 77 88" channel=can0 fields="0|Cmd|11|Message type;1-2|RPM|2233|Motor speed;3-4|Temp|4455|Motor temp"`
 */
@Composable
fun CanFrameConsoleWidget(spec: ConsoleWidgetSpec.CanFrame) {
    val meta = buildList {
        add("ID ${formatCanFrameId(spec.frameId, spec.extended)}")
        add(if (spec.extended) "EXT" else "STD")
        add("DLC ${spec.dlc}")
        if (spec.remote) add("RTR")
        if (spec.fd) add("CAN FD")
        if (spec.bitrateSwitch) add("BRS")
        spec.channel?.takeIf { it.isNotBlank() }?.let(::add)
    }.joinToString(" • ")

    FrameCard(
        backgroundColor = spec.backgroundColor,
        borderColor = spec.borderColor
    ) {
        FrameHeader(
            title = spec.title,
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
            textColor = spec.byteColor,
            emptyText = if (spec.remote) "Remote frame without payload" else "No payload"
        )

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

private fun formatCanFrameId(frameId: Int, extended: Boolean): String {
    val width = if (extended) 8 else 3
    return "0x${frameId.toUInt().toString(16).uppercase().padStart(width, '0')}"
}

@Preview(name = "CAN Frame", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewCanFrameConsoleWidget() {
    WidgetPreviewSurface {
        CanFrameConsoleWidget(previewCanFrameSpec())
    }
}
