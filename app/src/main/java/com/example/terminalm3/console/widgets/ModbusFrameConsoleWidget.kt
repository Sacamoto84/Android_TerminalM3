package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec
import com.example.terminalm3.console.ModbusDirection
import com.example.terminalm3.console.ModbusFieldRow

/**
 * Compose renderer for [ConsoleWidgetSpec.ModbusFrame].
 *
 * Request that creates this preview:
 * `ui type=modbus-frame title="Read Holding Registers" direction=request data="01 03 00 10 00 02 C5 CE" fields="0|Addr|01|Slave ID;1|Func|03|Read Holding;2-3|Start|0010|Address;4-5|Count|0002|Registers;6-7|CRC|C5CE|CRC16"`
 */
@Composable
fun ModbusFrameConsoleWidget(spec: ConsoleWidgetSpec.ModbusFrame) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            spec.title?.takeIf { it.isNotBlank() }?.let { title ->
                Text(
                    text = title,
                    color = spec.titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(spec.accentColor.copy(alpha = 0.18f))
                    .border(1.dp, spec.accentColor.copy(alpha = 0.42f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = modbusDirectionLabel(spec.direction),
                    color = spec.accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        spec.bytes.chunked(8).forEachIndexed { rowIndex, rowBytes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowBytes.forEachIndexed { byteIndex, byte ->
                    ByteCell(
                        index = rowIndex * 8 + byteIndex,
                        value = byte,
                        accentColor = spec.accentColor,
                        textColor = spec.byteColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(8 - rowBytes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        if (spec.fields.isNotEmpty()) {
            spec.fields.forEach { field ->
                ModbusFieldRowView(field = field, spec = spec)
            }
        }
    }
}

@Composable
private fun ByteCell(
    index: Int,
    value: Int,
    accentColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accentColor.copy(alpha = 0.10f))
            .border(1.dp, accentColor.copy(alpha = 0.22f), RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = index.toString(),
            color = accentColor,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value.toString(16).uppercase().padStart(2, '0'),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun ModbusFieldRowView(
    field: ModbusFieldRow,
    spec: ConsoleWidgetSpec.ModbusFrame
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(spec.backgroundColor.copy(alpha = 0.24f))
            .border(1.dp, spec.borderColor.copy(alpha = 0.72f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(spec.accentColor.copy(alpha = 0.18f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = field.range,
                color = spec.accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = field.name,
                color = spec.fieldNameColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            field.description?.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    color = spec.fieldDescriptionColor,
                    fontSize = 12.sp
                )
            }
        }

        field.value?.takeIf { it.isNotBlank() }?.let { value ->
            Text(
                text = value,
                color = spec.fieldMetaColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun modbusDirectionLabel(direction: ModbusDirection): String {
    return when (direction) {
        ModbusDirection.Request -> "REQUEST"
        ModbusDirection.Response -> "RESPONSE"
        ModbusDirection.Error -> "ERROR"
    }
}

@Preview(name = "Modbus Frame", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewModbusFrameConsoleWidget() {
    WidgetPreviewSurface {
        ModbusFrameConsoleWidget(previewModbusFrameSpec())
    }
}
