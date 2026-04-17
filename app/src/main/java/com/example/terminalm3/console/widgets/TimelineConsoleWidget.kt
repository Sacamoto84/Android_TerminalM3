package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.example.terminalm3.console.TimelineItem

/**
 * Compose renderer for [ConsoleWidgetSpec.Timeline].
 *
 * Request that creates this preview:
 * `ui type=timeline title="Boot" items="12:01 Boot|12:03 WiFi connected|12:05 MQTT online"`
 */
@Composable
fun TimelineConsoleWidget(spec: ConsoleWidgetSpec.Timeline) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        spec.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                color = spec.titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        spec.items.forEachIndexed { index, item ->
            TimelineRow(
                item = item,
                spec = spec,
                showTail = index != spec.items.lastIndex
            )
        }
    }
}

@Composable
private fun TimelineRow(
    item: TimelineItem,
    spec: ConsoleWidgetSpec.Timeline,
    showTail: Boolean
) {
    val accentColor = item.color ?: spec.lineColor

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(accentColor)
                    .padding(5.dp)
            )

            if (showTail) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(accentColor.copy(alpha = 0.45f))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item.time?.takeIf { it.isNotBlank() }?.let { time ->
                Text(
                    text = time,
                    color = spec.timeColor,
                    fontSize = 12.sp
                )
            }
            Text(
                text = item.text,
                color = spec.textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            item.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
                Text(
                    text = subtitle,
                    color = spec.subtitleColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(name = "Timeline", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewTimelineConsoleWidget() {
    WidgetPreviewSurface {
        TimelineConsoleWidget(previewTimelineSpec())
    }
}
