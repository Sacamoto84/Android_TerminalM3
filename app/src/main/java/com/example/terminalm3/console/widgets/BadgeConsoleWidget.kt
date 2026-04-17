package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.Badge].
 *
 * Request that creates this preview:
 * `ui type=badge text="READY" bg=#1F7A1F fg=#FFFFFF size=14`
 */
@Composable
fun BadgeConsoleWidget(spec: ConsoleWidgetSpec.Badge) {
    Text(
        text = spec.text,
        color = spec.textColor,
        fontSize = spec.fontSizeSp.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(spec.backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Preview(name = "Badge", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeConsoleWidget() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeSpec())
    }
}
