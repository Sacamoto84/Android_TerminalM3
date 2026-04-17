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
import androidx.compose.foundation.layout.width
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
 * Compose renderer for [ConsoleWidgetSpec.Panel].
 */
@Composable
fun PanelConsoleWidget(spec: ConsoleWidgetSpec.Panel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(spec.accentColor)
        )

        Spacer(modifier = Modifier.width(12.dp))
        WidgetOptionalIcon(spec.iconName, spec.title, 28)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = spec.title,
                color = spec.titleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            spec.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = spec.subtitleColor,
                    fontSize = 13.sp
                )
            }
        }

        spec.value?.takeIf { it.isNotBlank() }?.let { value ->
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = value,
                color = spec.valueColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(name = "Panel", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewPanelConsoleWidget() {
    WidgetPreviewSurface {
        PanelConsoleWidget(
            ConsoleWidgetSpec.Panel(
                title = "Motor 1",
                value = "READY",
                subtitle = "24.3V  1.8A",
                iconName = "info",
                accentColor = androidx.compose.ui.graphics.Color(0xFF36C36B)
            )
        )
    }
}
