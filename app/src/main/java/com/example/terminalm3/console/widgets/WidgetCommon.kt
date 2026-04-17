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
import com.example.terminalm3.console.KeyValueGridItem
import com.example.terminalm3.console.LedRowItem
import com.example.terminalm3.console.ModbusDirection
import com.example.terminalm3.console.ModbusFieldRow
import com.example.terminalm3.console.PinBankItem
import com.example.terminalm3.console.RegisterTableRow
import com.example.terminalm3.console.TimelineItem

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
 * `ui type=sparkline label="Temp" values="21,22,22,23,24,23,25" min=18 max=28 color=#36C36B display="25C" points=on`
 * `ui type=bar-group title="Motors" labels="M1|M2|M3" values="20|45|80" max=100 colors="#36C36B|#4FC3F7|#FFB300"`
 * `ui type=gauge label="CPU" value=72 max=100 unit="%" color=#36C36B`
 * `ui type=battery label="Battery A" value=78 max=100 charging=true voltage=4.08`
 * `ui type=led-row title="Links" items="NET:#00E676|MQTT:#00E676|ERR:#FF5252|GPS:off"`
 * `ui type=stats-card title="RPM" value=1450 unit="rpm" delta="+12" subtitle="Motor 1" accent=#36C36B`
 * `ui type=kv-grid title="Motor 1" items="Voltage:24.3V|Current:1.8A|Temp:62C|State:READY" columns=2`
 * `ui type=pin-bank title="GPIO" items="D1:on|D2:off|D3:warn|A0:adc|PWM1:pwm"`
 * `ui type=timeline title="Boot" items="12:01 Boot|12:03 WiFi connected|12:05 MQTT online"`
 * `ui type=line-chart title="Voltage" values="24.1,24.2,24.0,24.3,24.4" labels="T1|T2|T3|T4|T5" min=23 max=25 color=#4FC3F7`
 * `ui type=bitfield label="STATUS" value=0xB38F bits=16`
 * `ui type=hex-dump title="RX Buffer" data="48 65 6C 6C 6F 20 57 6F 72 6C 64" width=8 addr=0x1000 ascii=on`
 * `ui type=register-table title="Holding Registers" rows="0000|0x1234|Status;0001|0x00A5|Flags;0002|0x03E8|Speed"`
 * `ui type=modbus-frame title="Read Holding Registers" direction=request data="01 03 00 10 00 02 C5 CE" fields="0|Addr|01|Slave ID;1|Func|03|Read Holding;2-3|Start|0010|Address;4-5|Count|0002|Registers;6-7|CRC|C5CE|CRC16"`
 */
@Preview(
    name = "Widget Gallery",
    showBackground = true,
    backgroundColor = CONSOLE_WIDGET_PREVIEW_BG,
    widthDp = 420,
    heightDp = 4700
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
            SparklineConsoleWidget(previewSparklineSpec())
            BarGroupConsoleWidget(previewBarGroupSpec())
            GaugeConsoleWidget(previewGaugeSpec())
            BatteryConsoleWidget(previewBatterySpec())
            LedRowConsoleWidget(previewLedRowSpec())
            StatsCardConsoleWidget(previewStatsCardSpec())
            KeyValueGridConsoleWidget(previewKeyValueGridSpec())
            PinBankConsoleWidget(previewPinBankSpec())
            TimelineConsoleWidget(previewTimelineSpec())
            LineChartConsoleWidget(previewLineChartSpec())
            BitFieldConsoleWidget(previewBitFieldSpec())
            HexDumpConsoleWidget(previewHexDumpSpec())
            RegisterTableConsoleWidget(previewRegisterTableSpec())
            ModbusFrameConsoleWidget(previewModbusFrameSpec())
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

internal fun previewSparklineSpec() = ConsoleWidgetSpec.Sparkline(
    label = "Temp",
    values = listOf(21f, 22f, 22f, 23f, 24f, 23f, 25f),
    min = 18f,
    max = 28f,
    text = "25C",
    lineColor = Color(0xFF36C36B),
    fillColor = Color(0x2236C36B),
    showDots = true
)

internal fun previewBarGroupSpec() = ConsoleWidgetSpec.BarGroup(
    title = "Motors",
    labels = listOf("M1", "M2", "M3"),
    values = listOf(20f, 45f, 80f),
    max = 100f,
    colors = listOf(
        Color(0xFF36C36B),
        Color(0xFF4FC3F7),
        Color(0xFFFFB300)
    )
)

internal fun previewGaugeSpec() = ConsoleWidgetSpec.Gauge(
    label = "CPU",
    value = 72f,
    max = 100f,
    unit = "%",
    text = "72%",
    color = Color(0xFF36C36B)
)

internal fun previewBatterySpec() = ConsoleWidgetSpec.Battery(
    label = "Battery A",
    value = 78f,
    max = 100f,
    text = "78%",
    subtitle = "4.08V",
    charging = true
)

internal fun previewLedRowSpec() = ConsoleWidgetSpec.LedRow(
    title = "Links",
    items = listOf(
        LedRowItem("NET", Color(0xFF00E676), true),
        LedRowItem("MQTT", Color(0xFF00E676), true),
        LedRowItem("ERR", Color(0xFFFF5252), true),
        LedRowItem("GPS", Color(0xFF54616C), false)
    )
)

internal fun previewStatsCardSpec() = ConsoleWidgetSpec.StatsCard(
    title = "RPM",
    value = "1450",
    unit = "rpm",
    subtitle = "Motor 1",
    delta = "+12",
    accentColor = Color(0xFF36C36B)
)

internal fun previewKeyValueGridSpec() = ConsoleWidgetSpec.KeyValueGrid(
    title = "Motor 1",
    items = listOf(
        KeyValueGridItem("Voltage", "24.3V"),
        KeyValueGridItem("Current", "1.8A"),
        KeyValueGridItem("Temp", "62C"),
        KeyValueGridItem("State", "READY")
    ),
    columns = 2
)

internal fun previewPinBankSpec() = ConsoleWidgetSpec.PinBank(
    title = "GPIO",
    items = listOf(
        PinBankItem("D1", "ON", Color(0xFF36C36B), true),
        PinBankItem("D2", "OFF", Color(0xFF54616C), false),
        PinBankItem("D3", "WARN", Color(0xFFFFC107), true),
        PinBankItem("A0", "ADC", Color(0xFF4FC3F7), true),
        PinBankItem("PWM1", "PWM", Color(0xFFFFB300), true)
    ),
    columns = 3
)

internal fun previewTimelineSpec() = ConsoleWidgetSpec.Timeline(
    title = "Boot",
    items = listOf(
        TimelineItem("12:01", "Power applied"),
        TimelineItem("12:03", "WiFi connected"),
        TimelineItem("12:05", "MQTT online")
    )
)

internal fun previewLineChartSpec() = ConsoleWidgetSpec.LineChart(
    title = "Voltage",
    values = listOf(24.1f, 24.2f, 24.0f, 24.3f, 24.4f),
    labels = listOf("T1", "T2", "T3", "T4", "T5"),
    min = 23f,
    max = 25f,
    color = Color(0xFF4FC3F7),
    fillColor = Color(0x224FC3F7)
)

internal fun previewBitFieldSpec() = ConsoleWidgetSpec.BitField(
    label = "STATUS",
    value = 0xB38Fu,
    bitCount = 16
)

internal fun previewHexDumpSpec() = ConsoleWidgetSpec.HexDump(
    title = "RX Buffer",
    bytes = listOf(
        0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x57, 0x6F,
        0x72, 0x6C, 0x64, 0x21, 0x00, 0x1B, 0x0D, 0x0A
    ),
    bytesPerRow = 8,
    startAddress = 0x1000,
    showAscii = true
)

internal fun previewRegisterTableSpec() = ConsoleWidgetSpec.RegisterTable(
    title = "Holding Registers",
    rows = listOf(
        RegisterTableRow("0000", "0x1234", "Status"),
        RegisterTableRow("0001", "0x00A5", "Flags"),
        RegisterTableRow("0002", "0x03E8", "Speed")
    )
)

internal fun previewModbusFrameSpec() = ConsoleWidgetSpec.ModbusFrame(
    title = "Read Holding Registers",
    direction = ModbusDirection.Request,
    bytes = listOf(0x01, 0x03, 0x00, 0x10, 0x00, 0x02, 0xC5, 0xCE),
    fields = listOf(
        ModbusFieldRow("0", "Addr", "01", "Slave ID"),
        ModbusFieldRow("1", "Func", "03", "Read Holding"),
        ModbusFieldRow("2-3", "Start", "0010", "Address"),
        ModbusFieldRow("4-5", "Count", "0002", "Registers"),
        ModbusFieldRow("6-7", "CRC", "C5CE", "CRC16")
    ),
    accentColor = Color(0xFF4FC3F7)
)
