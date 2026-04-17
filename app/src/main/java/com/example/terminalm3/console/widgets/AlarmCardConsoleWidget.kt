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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.AlarmSeverity
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.AlarmCard].
 *
 * Request that creates this preview:
 * `ui type=alarm-card title="Overheat" message="Motor 1 temperature reached 92C" severity=critical time="12:41:03" icon=warn2`
 */
@Composable
fun AlarmCardConsoleWidget(spec: ConsoleWidgetSpec.AlarmCard) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(54.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(spec.accentColor)
        )

        Spacer(modifier = Modifier.width(12.dp))
        WidgetOptionalIcon(spec.iconName, spec.title, 26)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = severityLabel(spec.severity),
                color = spec.accentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = spec.title,
                color = spec.titleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            spec.message?.takeIf { it.isNotBlank() }?.let { message ->
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = message,
                    color = spec.messageColor,
                    fontSize = 13.sp
                )
            }
        }

        spec.timestamp?.takeIf { it.isNotBlank() }?.let { timestamp ->
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = timestamp,
                color = spec.metaColor,
                fontSize = 12.sp,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(name = "Alarm Card", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewAlarmCardConsoleWidget() {
    WidgetPreviewSurface {
        AlarmCardConsoleWidget(previewAlarmCardSpec())
    }
}
