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

/**
 * Compose renderer for [ConsoleWidgetSpec.BitField].
 *
 * Request that creates this preview:
 * `ui type=bitfield label="STATUS" value=0xB38F bits=16`
 */
@Composable
fun BitFieldConsoleWidget(spec: ConsoleWidgetSpec.BitField) {
    val hexDigits = ((spec.bitCount + 3) / 4).coerceAtLeast(2)
    val bitIndexes = (spec.bitCount - 1 downTo 0).toList()
    val rows = bitIndexes.chunked(8)

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
            spec.label?.takeIf { it.isNotBlank() }?.let { label ->
                Text(
                    text = label,
                    color = spec.labelColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "0x${spec.value.toString(16).uppercase().padStart(hexDigits, '0')}",
                color = spec.valueColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        }

        rows.forEach { rowBits ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    rowBits.forEach { bit ->
                        Text(
                            text = bit.toString(),
                            color = spec.indexColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    rowBits.forEach { bit ->
                        val isSet = ((spec.value shr bit) and 1uL) == 1uL
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSet) spec.setColor else spec.clearColor)
                                .border(1.dp, spec.borderColor.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isSet) "1" else "0",
                                color = if (isSet) spec.backgroundColor else spec.valueColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Bit Field", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewBitFieldConsoleWidget() {
    WidgetPreviewSurface {
        BitFieldConsoleWidget(previewBitFieldSpec())
    }
}
