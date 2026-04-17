package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

/**
 * Compose renderer for [ConsoleWidgetSpec.Progress].
 */
@Composable
fun ProgressConsoleWidget(spec: ConsoleWidgetSpec.Progress) {
    val fraction = (spec.value / spec.max).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        if (!spec.label.isNullOrBlank() || !spec.text.isNullOrBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                spec.label?.takeIf { it.isNotBlank() }?.let { label ->
                    Text(
                        text = label,
                        color = spec.labelColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                } ?: Spacer(modifier = Modifier.weight(1f))

                spec.text?.takeIf { it.isNotBlank() }?.let { valueText ->
                    Text(
                        text = valueText,
                        color = spec.valueColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(spec.trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(spec.fillColor)
            )
        }
    }
}

@Preview(name = "Progress", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewProgressConsoleWidget() {
    WidgetPreviewSurface {
        ProgressConsoleWidget(
            ConsoleWidgetSpec.Progress(
                label = "Battery",
                value = 72f,
                max = 100f,
                text = "72%",
                fillColor = androidx.compose.ui.graphics.Color(0xFF36C36B)
            )
        )
    }
}
