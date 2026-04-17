package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec
import com.example.terminalm3.console.LedRowItem

/**
 * Compose renderer for [ConsoleWidgetSpec.LedRow].
 *
 * Request that creates this preview:
 * `ui type=led-row title="Links" items="NET:#00E676|MQTT:#00E676|ERR:#FF5252|GPS:off"`
 */
@Composable
fun LedRowConsoleWidget(spec: ConsoleWidgetSpec.LedRow) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        spec.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                color = spec.titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        spec.items.chunked(3).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    LedRowChip(
                        item = item,
                        spec = spec,
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            if (rowIndex != spec.items.chunked(3).lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun LedRowChip(
    item: LedRowItem,
    spec: ConsoleWidgetSpec.LedRow,
    modifier: Modifier = Modifier
) {
    val indicatorColor = if (item.isActive) item.color else spec.offColor
    val chipBackground = if (item.isActive) {
        spec.backgroundColor.copy(alpha = 0.36f)
    } else {
        spec.backgroundColor.copy(alpha = 0.18f)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(chipBackground)
            .border(1.dp, indicatorColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(indicatorColor)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = item.label,
            color = if (item.isActive) spec.labelColor else spec.labelColor.copy(alpha = 0.72f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(name = "Led Row", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewLedRowConsoleWidget() {
    WidgetPreviewSurface {
        LedRowConsoleWidget(previewLedRowSpec())
    }
}
