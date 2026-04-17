package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.Battery].
 *
 * Request that creates this preview:
 * `ui type=battery label="Battery A" value=78 max=100 charging=true voltage=4.08`
 */
@Composable
fun BatteryConsoleWidget(spec: ConsoleWidgetSpec.Battery) {
    val fraction = (spec.value / spec.max).coerceIn(0f, 1f)
    val fillColor = spec.fillColor ?: when {
        fraction <= 0.20f -> Color(0xFFFF5252)
        fraction <= 0.45f -> Color(0xFFFFC107)
        else -> Color(0xFF36C36B)
    }
    val displayFraction = if (fraction == 0f) 0f else fraction.coerceAtLeast(0.08f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            spec.label?.takeIf { it.isNotBlank() }?.let { label ->
                Text(
                    text = label,
                    color = spec.labelColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            spec.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = spec.subtitleColor,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = spec.text ?: "${(fraction * 100f).toInt()}%",
                color = spec.valueColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (spec.charging) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "CHG",
                    color = fillColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, spec.borderColor, RoundedCornerShape(6.dp))
                        .background(spec.trackColor.copy(alpha = 0.30f))
                        .padding(2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(displayFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(fillColor)
                    )
                }

                Spacer(modifier = Modifier.width(3.dp))

                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(11.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(spec.borderColor)
                )
            }
        }
    }
}

@Preview(name = "Battery", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewBatteryConsoleWidget() {
    WidgetPreviewSurface {
        BatteryConsoleWidget(previewBatterySpec())
    }
}
