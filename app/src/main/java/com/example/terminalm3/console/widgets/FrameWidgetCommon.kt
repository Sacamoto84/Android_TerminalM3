package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.FrameDirection
import com.example.terminalm3.console.ModbusDirection

internal data class FrameFieldUi(
    val range: String,
    val name: String,
    val value: String? = null,
    val description: String? = null
)

@Composable
internal fun FrameCard(
    backgroundColor: Color,
    borderColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
internal fun FrameHeader(
    title: String?,
    titleColor: Color,
    accentColor: Color,
    chipText: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        title?.takeIf { it.isNotBlank() }?.let { safeTitle ->
            Text(
                text = safeTitle,
                color = titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
        } ?: Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(accentColor.copy(alpha = 0.18f))
                .border(1.dp, accentColor.copy(alpha = 0.42f), RoundedCornerShape(999.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = chipText,
                color = accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun FrameMetaLine(
    text: String?,
    color: Color
) {
    text?.takeIf { it.isNotBlank() }?.let { safeText ->
        Text(
            text = safeText,
            color = color,
            fontSize = 12.sp
        )
    }
}

@Composable
internal fun FrameByteGrid(
    bytes: List<Int>,
    accentColor: Color,
    textColor: Color,
    columns: Int = 8,
    emptyText: String? = null
) {
    if (bytes.isEmpty()) {
        emptyText?.takeIf { it.isNotBlank() }?.let { safeText ->
            Text(
                text = safeText,
                color = accentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    bytes.chunked(columns).forEachIndexed { rowIndex, rowBytes ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            rowBytes.forEachIndexed { byteIndex, byte ->
                FrameByteCell(
                    index = rowIndex * columns + byteIndex,
                    value = byte,
                    accentColor = accentColor,
                    textColor = textColor,
                    modifier = Modifier.weight(1f)
                )
            }

            repeat(columns - rowBytes.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FrameByteCell(
    index: Int,
    value: Int,
    accentColor: Color,
    textColor: Color,
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
internal fun FrameFieldList(
    fields: List<FrameFieldUi>,
    accentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    nameColor: Color,
    metaColor: Color,
    descriptionColor: Color
) {
    fields.forEach { field ->
        FrameFieldRowView(
            field = field,
            accentColor = accentColor,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            nameColor = nameColor,
            metaColor = metaColor,
            descriptionColor = descriptionColor
        )
    }
}

@Composable
private fun FrameFieldRowView(
    field: FrameFieldUi,
    accentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    nameColor: Color,
    metaColor: Color,
    descriptionColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor.copy(alpha = 0.24f))
            .border(1.dp, borderColor.copy(alpha = 0.72f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.18f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = field.range.ifBlank { "--" },
                color = accentColor,
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
                color = nameColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            field.description?.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    color = descriptionColor,
                    fontSize = 12.sp
                )
            }
        }

        field.value?.takeIf { it.isNotBlank() }?.let { value ->
            Text(
                text = value,
                color = metaColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
internal fun FrameAsciiBlock(
    bytes: List<Int>,
    accentColor: Color,
    labelColor: Color,
    textColor: Color,
    label: String = "ASCII"
) {
    if (bytes.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .border(1.dp, accentColor.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = bytes.joinToString("") { byte -> byte.toPrintableAscii() },
            color = textColor,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

internal fun frameDirectionLabel(direction: FrameDirection): String {
    return when (direction) {
        FrameDirection.Tx -> "TX"
        FrameDirection.Rx -> "RX"
        FrameDirection.Error -> "ERROR"
    }
}

internal fun modbusDirectionLabel(direction: ModbusDirection): String {
    return when (direction) {
        ModbusDirection.Request -> "REQUEST"
        ModbusDirection.Response -> "RESPONSE"
        ModbusDirection.Error -> "ERROR"
    }
}

private fun Int.toPrintableAscii(): String {
    return if (this in 32..126) {
        this.toChar().toString()
    } else {
        "."
    }
}
