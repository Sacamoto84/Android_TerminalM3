package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
 * Compose renderer for [ConsoleWidgetSpec.Dot].
 */
@Composable
fun DotConsoleWidget(spec: ConsoleWidgetSpec.Dot) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .size(spec.sizeDp.dp)
                .clip(CircleShape)
                .background(spec.color)
        )

        spec.label?.takeIf { it.isNotBlank() }?.let { label ->
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = spec.labelColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(name = "Dot", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewDotConsoleWidget() {
    WidgetPreviewSurface {
        DotConsoleWidget(
            ConsoleWidgetSpec.Dot(
                color = androidx.compose.ui.graphics.Color(0xFF00E676),
                sizeDp = 16,
                label = "Link active"
            )
        )
    }
}
