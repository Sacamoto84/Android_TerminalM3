package com.example.terminalm3.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Preview gallery for all console widgets.
 *
 * Open this file in Android Studio and use the Preview panel to inspect the
 * widgets without running the app on a device.
 */
@Preview(
    name = "Widget Gallery",
    showBackground = true,
    backgroundColor = 0xFF090909,
    widthDp = 420,
)
@Composable
private fun PreviewConsoleWidgetGallery() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ConsoleWidget(previewBadgeSpec())
            ConsoleWidget(previewDotSpec())
            ConsoleWidget(previewImageSpec())
            ConsoleWidget(previewPanelSpec())
            ConsoleWidget(previewProgressSpec())
            ConsoleWidget(previewTwoColumnSpec())
            ConsoleWidget(previewTableSpec())
            ConsoleWidget(previewSwitchSpec())
            ConsoleWidget(previewAlarmCardSpec())
        }
    }
}

@Preview(name = "Badge", showBackground = true, backgroundColor = 0xFF090909, widthDp = 360)
@Composable
private fun PreviewConsoleWidgetBadge() {
    PreviewSurface { ConsoleWidget(previewBadgeSpec()) }
}

@Preview(name = "Dot", showBackground = true, backgroundColor = 0xFF090909, widthDp = 360)
@Composable
private fun PreviewConsoleWidgetDot() {
    PreviewSurface { ConsoleWidget(previewDotSpec()) }
}

@Preview(name = "Image", showBackground = true, backgroundColor = 0xFF090909, widthDp = 360)
@Composable
private fun PreviewConsoleWidgetImage() {
    PreviewSurface { ConsoleWidget(previewImageSpec()) }
}

@Preview(name = "Panel", showBackground = true, backgroundColor = 0xFF090909, widthDp = 420)
@Composable
private fun PreviewConsoleWidgetPanel() {
    PreviewSurface { ConsoleWidget(previewPanelSpec()) }
}

@Preview(name = "Progress", showBackground = true, backgroundColor = 0xFF090909, widthDp = 420)
@Composable
private fun PreviewConsoleWidgetProgress() {
    PreviewSurface { ConsoleWidget(previewProgressSpec()) }
}

@Preview(name = "Two Column", showBackground = true, backgroundColor = 0xFF090909, widthDp = 420)
@Composable
private fun PreviewConsoleWidgetTwoColumn() {
    PreviewSurface { ConsoleWidget(previewTwoColumnSpec()) }
}

@Preview(name = "Table", showBackground = true, backgroundColor = 0xFF090909, widthDp = 420)
@Composable
private fun PreviewConsoleWidgetTable() {
    PreviewSurface { ConsoleWidget(previewTableSpec()) }
}

@Preview(name = "Switch", showBackground = true, backgroundColor = 0xFF090909, widthDp = 420)
@Composable
private fun PreviewConsoleWidgetSwitch() {
    PreviewSurface { ConsoleWidget(previewSwitchSpec()) }
}

@Preview(name = "Alarm Card", showBackground = true, backgroundColor = 0xFF090909, widthDp = 420)
@Composable
private fun PreviewConsoleWidgetAlarmCard() {
    PreviewSurface { ConsoleWidget(previewAlarmCardSpec()) }
}

@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF090909))
            .padding(16.dp)
    ) {
        content()
    }
}

private fun previewBadgeSpec() = ConsoleWidgetSpec.Badge(
    text = "READY",
    backgroundColor = Color(0xFF1F7A1F)
)

private fun previewDotSpec() = ConsoleWidgetSpec.Dot(
    color = Color(0xFF00E676),
    sizeDp = 16,
    label = "Link active"
)

private fun previewImageSpec() = ConsoleWidgetSpec.Image(
    drawableName = "info",
    sizeDp = 40,
    description = "Info icon"
)

private fun previewPanelSpec() = ConsoleWidgetSpec.Panel(
    title = "Motor 1",
    value = "READY",
    subtitle = "24.3V  1.8A",
    iconName = "info",
    accentColor = Color(0xFF36C36B)
)

private fun previewProgressSpec() = ConsoleWidgetSpec.Progress(
    label = "Battery",
    value = 72f,
    max = 100f,
    text = "72%",
    fillColor = Color(0xFF36C36B)
)

private fun previewTwoColumnSpec() = ConsoleWidgetSpec.TwoColumn(
    left = "Voltage",
    right = "24.3V"
)

private fun previewTableSpec() = ConsoleWidgetSpec.Table(
    headers = listOf("Name", "State", "Temp"),
    rows = listOf(
        listOf("M1", "READY", "24.3"),
        listOf("M2", "WAIT", "22.9"),
        listOf("M3", "ALARM", "91.8")
    )
)

private fun previewSwitchSpec() = ConsoleWidgetSpec.Switch(
    label = "Pump enable",
    checked = true,
    subtitle = "Remote mode"
)

private fun previewAlarmCardSpec() = ConsoleWidgetSpec.AlarmCard(
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
