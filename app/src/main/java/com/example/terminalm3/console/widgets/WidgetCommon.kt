package com.example.terminalm3.console.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.AlarmSeverity
import com.example.terminalm3.console.ConsoleWidgetSpec

internal const val CONSOLE_WIDGET_PREVIEW_BG = 0xFF090909

@Composable
internal fun WidgetPreviewSurface(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(CONSOLE_WIDGET_PREVIEW_BG))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
internal fun WidgetOptionalIcon(iconName: String?, contentDescription: String, sizeDp: Int) {
    iconName?.takeIf { it.isNotBlank() }?.let { safeName ->
        val drawableId = rememberWidgetDrawableId(safeName)
        if (drawableId != 0) {
            Image(
                painter = painterResource(drawableId),
                contentDescription = contentDescription,
                modifier = Modifier.size(sizeDp.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
internal fun MissingDrawableWidget(drawableName: String) {
    Text(
        text = "Drawable not found: $drawableName",
        color = Color(0xFFFF8A80),
        fontSize = 13.sp
    )
}

@Composable
internal fun rememberWidgetDrawableId(drawableName: String): Int {
    val context = LocalContext.current
    return remember(drawableName) {
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)
    }
}

internal fun List<String>.normalizeWidgetRow(columnCount: Int): List<String> {
    return if (size >= columnCount) {
        take(columnCount)
    } else {
        this + List(columnCount - size) { "" }
    }
}

internal fun severityLabel(severity: AlarmSeverity): String {
    return when (severity) {
        AlarmSeverity.Info -> "INFO"
        AlarmSeverity.Warn -> "WARN"
        AlarmSeverity.Error -> "ERROR"
        AlarmSeverity.Critical -> "CRITICAL"
    }
}

/**
 * Gallery preview for all console widgets.
 *
 * Requests that recreate these preview samples:
 * `ui type=badge text="READY" bg=#1F7A1F fg=#FFFFFF size=14`
 * `ui type=dot color=#00E676 size=16 label="Link active"`
 * `ui type=image name=info size=40 desc="Info icon"`
 * `ui type=panel title="Motor 1" value=READY subtitle="24.3V 1.8A" accent=#36C36B icon=info`
 * `ui type=progress label="Battery" value=72 max=100 fill=#36C36B display="72%"`
 * `ui type=2col left="Voltage" right="24.3V"`
 * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9;M3|ALARM|91.8"`
 * `ui type=switch label="Pump enable" state=on subtitle="Remote mode"`
 * `ui type=alarm-card title="Overheat" message="Motor 1 temperature reached 92C" severity=critical time="12:41:03" icon=warn2`
 */
@Preview(
    name = "Widget Gallery",
    showBackground = true,
    backgroundColor = CONSOLE_WIDGET_PREVIEW_BG,
    widthDp = 420,
    heightDp = 1400
)
@Composable
internal fun PreviewConsoleWidgetGallery() {
    WidgetPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BadgeConsoleWidget(previewBadgeSpec())
            DotConsoleWidget(previewDotSpec())
            ImageConsoleWidget(previewImageSpec())
            PanelConsoleWidget(previewPanelSpec())
            ProgressConsoleWidget(previewProgressSpec())
            TwoColumnConsoleWidget(previewTwoColumnSpec())
            TableConsoleWidget(previewTableSpec())
            SwitchConsoleWidget(previewSwitchSpec())
            AlarmCardConsoleWidget(previewAlarmCardSpec())
        }
    }
}

internal fun previewBadgeSpec() = ConsoleWidgetSpec.Badge(
    text = "READY",
    backgroundColor = Color(0xFF1F7A1F)
)

internal fun previewDotSpec() = ConsoleWidgetSpec.Dot(
    color = Color(0xFF00E676),
    sizeDp = 16,
    label = "Link active"
)

internal fun previewImageSpec() = ConsoleWidgetSpec.Image(
    drawableName = "info",
    sizeDp = 40,
    description = "Info icon"
)

internal fun previewPanelSpec() = ConsoleWidgetSpec.Panel(
    title = "Motor 1",
    value = "READY",
    subtitle = "24.3V 1.8A",
    iconName = "info",
    accentColor = Color(0xFF36C36B)
)

internal fun previewProgressSpec() = ConsoleWidgetSpec.Progress(
    label = "Battery",
    value = 72f,
    max = 100f,
    text = "72%",
    fillColor = Color(0xFF36C36B)
)

internal fun previewTwoColumnSpec() = ConsoleWidgetSpec.TwoColumn(
    left = "Voltage",
    right = "24.3V"
)

internal fun previewTableSpec() = ConsoleWidgetSpec.Table(
    headers = listOf("Name", "State", "Temp"),
    rows = listOf(
        listOf("M1", "READY", "24.3"),
        listOf("M2", "WAIT", "22.9"),
        listOf("M3", "ALARM", "91.8")
    )
)

internal fun previewSwitchSpec() = ConsoleWidgetSpec.Switch(
    label = "Pump enable",
    checked = true,
    subtitle = "Remote mode"
)

internal fun previewAlarmCardSpec() = ConsoleWidgetSpec.AlarmCard(
    title = "Overheat",
    message = "Motor 1 temperature reached 92C",
    severity = AlarmSeverity.Critical,
    timestamp = "12:41:03",
    accentColor = Color(0xFFFF1744),
    backgroundColor = Color(0xFF280812),
    borderColor = Color(0xFF7B1730),
    titleColor = Color.White,
    messageColor = Color(0xFFFFD8E1),
    metaColor = Color(0xFFFF9AB0),
    iconName = "warn2"
)
