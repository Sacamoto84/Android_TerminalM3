package com.example.terminalm3.console

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.terminalm3.console.widgets.AlarmCardConsoleWidget
import com.example.terminalm3.console.widgets.BadgeConsoleWidget
import com.example.terminalm3.console.widgets.BarGroupConsoleWidget
import com.example.terminalm3.console.widgets.BatteryConsoleWidget
import com.example.terminalm3.console.widgets.BitFieldConsoleWidget
import com.example.terminalm3.console.widgets.CanFrameConsoleWidget
import com.example.terminalm3.console.widgets.DotConsoleWidget
import com.example.terminalm3.console.widgets.GaugeConsoleWidget
import com.example.terminalm3.console.widgets.HexDumpConsoleWidget
import com.example.terminalm3.console.widgets.ImageConsoleWidget
import com.example.terminalm3.console.widgets.KeyValueGridConsoleWidget
import com.example.terminalm3.console.widgets.LedRowConsoleWidget
import com.example.terminalm3.console.widgets.LineChartConsoleWidget
import com.example.terminalm3.console.widgets.ModbusFrameConsoleWidget
import com.example.terminalm3.console.widgets.PacketFrameConsoleWidget
import com.example.terminalm3.console.widgets.PanelConsoleWidget
import com.example.terminalm3.console.widgets.PinBankConsoleWidget
import com.example.terminalm3.console.widgets.ProgressConsoleWidget
import com.example.terminalm3.console.widgets.RegisterTableConsoleWidget
import com.example.terminalm3.console.widgets.SparklineConsoleWidget
import com.example.terminalm3.console.widgets.StatsCardConsoleWidget
import com.example.terminalm3.console.widgets.SwitchConsoleWidget
import com.example.terminalm3.console.widgets.TableConsoleWidget
import com.example.terminalm3.console.widgets.TimelineConsoleWidget
import com.example.terminalm3.console.widgets.TwoColumnConsoleWidget

/**
 * Р С›Р С—Р С‘РЎРѓР В°Р Р…Р С‘Р Вµ Р С”Р С•Р Р…РЎРѓР С•Р В»РЎРЉР Р…Р С•Р С–Р С• Р Р†Р С‘Р Т‘Р В¶Р ВµРЎвЂљР В° Р Р† Р Р†Р С‘Р Т‘Р Вµ Р Т‘Р В°Р Р…Р Р…РЎвЂ№РЎвЂ¦, Р С”Р С•РЎвЂљР С•РЎР‚Р С•Р Вµ Р С—Р С•РЎвЂљР С•Р С Р СР С•Р В¶Р Р…Р С• Р С•РЎвЂљРЎР‚Р С‘РЎРѓР С•Р Р†Р В°РЎвЂљРЎРЉ РЎвЂЎР ВµРЎР‚Р ВµР В· Compose.
 *
 * Р СљР С‘Р С”РЎР‚Р С•Р С”Р С•Р Р…РЎвЂљРЎР‚Р С•Р В»Р В»Р ВµРЎР‚ Р С•РЎвЂљР С—РЎР‚Р В°Р Р†Р В»РЎРЏР ВµРЎвЂљ РЎвЂљР ВµР С”РЎРѓРЎвЂљР С•Р Р†РЎС“РЎР‹ Р С”Р С•Р СР В°Р Р…Р Т‘РЎС“, Р С—РЎР‚Р С‘Р В»Р С•Р В¶Р ВµР Р…Р С‘Р Вµ РЎР‚Р В°Р В·Р В±Р С‘РЎР‚Р В°Р ВµРЎвЂљ Р ВµР Вµ Р Р† Р С•Р Т‘Р С‘Р Р…
 * Р С‘Р В· РЎРЊРЎвЂљР С‘РЎвЂ¦ `spec`-Р С•Р В±РЎР‰Р ВµР С”РЎвЂљР С•Р Р†, Р В° Р В·Р В°РЎвЂљР ВµР С [ConsoleWidget] Р С—Р ВµРЎР‚Р ВµР Т‘Р В°Р ВµРЎвЂљ Р С•РЎвЂљРЎР‚Р С‘РЎРѓР С•Р Р†Р С”РЎС“
 * РЎРѓР С•Р С•РЎвЂљР Р†Р ВµРЎвЂљРЎРѓРЎвЂљР Р†РЎС“РЎР‹РЎвЂ°Р ВµР СРЎС“ Compose-Р Р†Р С‘Р Т‘Р В¶Р ВµРЎвЂљРЎС“ Р С‘Р В· Р С—Р В°Р С”Р ВµРЎвЂљР В° `console/widgets`.
 */
sealed interface ConsoleWidgetSpec {
    /**
     * Р С™Р С•Р СР С—Р В°Р С”РЎвЂљР Р…Р В°РЎРЏ Р С•Р С”РЎР‚РЎС“Р С–Р В»Р В°РЎРЏ Р С—Р В»Р В°РЎв‚¬Р С”Р В° Р Т‘Р В»РЎРЏ Р С”Р С•РЎР‚Р С•РЎвЂљР С”Р С‘РЎвЂ¦ РЎРѓРЎвЂљР В°РЎвЂљРЎС“РЎРѓР С•Р Р†, Р Р…Р В°Р С—РЎР‚Р С‘Р СР ВµРЎР‚ READY / OK / FAIL.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=badge text="READY" st=ok`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Badge(text = "READY"))`
     */
    data class Badge(
        val text: String,
        val textColor: Color = Color.White,
        val backgroundColor: Color = Color(0xFF1F7A1F),
        val fontSizeSp: Int = 14
    ) : ConsoleWidgetSpec

    /**
     * Р С™РЎР‚РЎС“Р С–Р В»РЎвЂ№Р в„– Р С‘Р Р…Р Т‘Р С‘Р С”Р В°РЎвЂљР С•РЎР‚, Р С”Р С•РЎвЂљР С•РЎР‚РЎвЂ№Р в„– Р С—РЎР‚Р С‘ Р В¶Р ВµР В»Р В°Р Р…Р С‘Р С‘ Р СР С•Р В¶Р ВµРЎвЂљ Р С—Р С•Р С”Р В°Р В·РЎвЂ№Р Р†Р В°РЎвЂљРЎРЉ Р С—Р С•Р Т‘Р С—Р С‘РЎРѓРЎРЉ РЎРѓР С—РЎР‚Р В°Р Р†Р В°.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=dot color=#00FF66 size=16 label="Link active"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Dot(color = Color.Green, label = "Link"))`
     */
    data class Dot(
        val color: Color = Color.Green,
        val sizeDp: Int = 14,
        val label: String? = null,
        val labelColor: Color = Color.White
    ) : ConsoleWidgetSpec

    /**
     * Drawable-РЎР‚Р ВµРЎРѓРЎС“РЎР‚РЎРѓ Р С‘Р В· `res/drawable`.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=image name=info size=32`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Image(drawableName = "info"))`
     */
    data class Image(
        val drawableName: String,
        val sizeDp: Int = 32,
        val description: String? = null
    ) : ConsoleWidgetSpec

    /**
     * Р В Р В°РЎРѓРЎв‚¬Р С‘РЎР‚Р ВµР Р…Р Р…Р В°РЎРЏ РЎРѓРЎвЂљР В°РЎвЂљРЎС“РЎРѓР Р…Р В°РЎРЏ Р С”Р В°РЎР‚РЎвЂљР С•РЎвЂЎР С”Р В° РЎРѓ Р В·Р В°Р С–Р С•Р В»Р С•Р Р†Р С”Р С•Р С, Р Р…Р ВµР С•Р В±РЎРЏР В·Р В°РЎвЂљР ВµР В»РЎРЉР Р…РЎвЂ№Р С Р С—Р С•Р Т‘Р В·Р В°Р С–Р С•Р В»Р С•Р Р†Р С”Р С•Р С,
     * Р В·Р Р…Р В°РЎвЂЎР ВµР Р…Р С‘Р ВµР С Р С‘ Р С‘Р С”Р С•Р Р…Р С”Р С•Р в„–.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=panel title="Motor 1" value=READY subtitle="24.3V" accent=#36C36B icon=info`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Panel(title = "Motor 1", value = "READY"))`
     */
    data class Panel(
        val title: String,
        val value: String? = null,
        val subtitle: String? = null,
        val accentColor: Color = Color(0xFF4CAF50),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val valueColor: Color = Color.White,
        val subtitleColor: Color = Color(0xFFB5C0C8),
        val iconName: String? = null
    ) : ConsoleWidgetSpec

    /**
     * Р С™Р В°РЎР‚РЎвЂљР С•РЎвЂЎР С”Р В° РЎРѓ Р С—Р С•Р В»Р С•РЎРѓР С•Р в„– Р С—РЎР‚Р С•Р С–РЎР‚Р ВµРЎРѓРЎРѓР В° Р Т‘Р В»РЎРЏ Р В±Р В°РЎвЂљР В°РЎР‚Р ВµР С‘, Р В·Р В°Р С–РЎР‚РЎС“Р В·Р С”Р С‘, Р С—РЎР‚Р С•РЎвЂ Р ВµР Р…РЎвЂљР В° Р Р†РЎвЂ№Р С—Р С•Р В»Р Р…Р ВµР Р…Р С‘РЎРЏ Р С‘ РЎвЂљ.Р Т‘.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=progress label="Battery" value=72 max=100 fill=#36C36B display="72%"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Progress(label = "Battery", value = 72f))`
     */
    data class Progress(
        val label: String? = null,
        val value: Float,
        val max: Float = 100f,
        val text: String? = null,
        val fillColor: Color = Color(0xFF3AC972),
        val trackColor: Color = Color(0xFF1A242B),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val labelColor: Color = Color.White,
        val valueColor: Color = Color(0xFFBDEFCF)
    ) : ConsoleWidgetSpec

    /**
     * Р вЂќР Р†РЎС“РЎвЂ¦Р С”Р С•Р В»Р С•Р Р…Р С•РЎвЂЎР Р…Р В°РЎРЏ РЎРѓРЎвЂљРЎР‚Р С•Р С”Р В° Р Т‘Р В»РЎРЏ РЎвЂљР ВµР В»Р ВµР СР ВµРЎвЂљРЎР‚Р С‘Р С‘ РЎвЂћР С•РЎР‚Р СР В°РЎвЂљР В° `Р С”Р В»РЎР‹РЎвЂЎ -> Р В·Р Р…Р В°РЎвЂЎР ВµР Р…Р С‘Р Вµ`.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=2col left="Voltage" right="24.3V"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.TwoColumn(left = "Voltage", right = "24.3V"))`
     */
    data class TwoColumn(
        val left: String,
        val right: String,
        val leftColor: Color = Color(0xFFB5C0C8),
        val rightColor: Color = Color.White,
        val backgroundColor: Color = Color(0xFF10151A),
        val borderColor: Color = Color(0xFF202C35)
    ) : ConsoleWidgetSpec

    /**
     * Р СћР В°Р В±Р В»Р С‘РЎвЂ Р В° РЎРѓ Р Р…Р ВµР С•Р В±РЎРЏР В·Р В°РЎвЂљР ВµР В»РЎРЉР Р…РЎвЂ№Р СР С‘ Р В·Р В°Р С–Р С•Р В»Р С•Р Р†Р С”Р В°Р СР С‘ Р С‘ Р Р…Р ВµРЎРѓР С”Р С•Р В»РЎРЉР С”Р С‘Р СР С‘ РЎРѓРЎвЂљРЎР‚Р С•Р С”Р В°Р СР С‘.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Table(headers = listOf("Name"), rows = listOf(listOf("M1"))))`
     */
    data class Table(
        val headers: List<String> = emptyList(),
        val rows: List<List<String>>,
        val backgroundColor: Color = Color(0xFF10151A),
        val borderColor: Color = Color(0xFF25323B),
        val headerBackgroundColor: Color = Color(0xFF1A2630),
        val headerTextColor: Color = Color.White,
        val cellTextColor: Color = Color(0xFFE3EEF5)
    ) : ConsoleWidgetSpec

    /**
     * Р вЂ™Р С‘Р В·РЎС“Р В°Р В»РЎРЉР Р…РЎвЂ№Р в„– Р С—Р ВµРЎР‚Р ВµР С”Р В»РЎР‹РЎвЂЎР В°РЎвЂљР ВµР В»РЎРЉ ON/OFF.
     * Р С›Р Р… Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·РЎС“Р ВµРЎвЂљРЎРѓРЎРЏ РЎвЂљР С•Р В»РЎРЉР С”Р С• Р Т‘Р В»РЎРЏ Р С•РЎвЂљР С•Р В±РЎР‚Р В°Р В¶Р ВµР Р…Р С‘РЎРЏ Р С‘ Р Р…Р Вµ Р С•Р В±РЎР‚Р В°Р В±Р В°РЎвЂљРЎвЂ№Р Р†Р В°Р ВµРЎвЂљ Р Р…Р В°Р В¶Р В°РЎвЂљР С‘РЎРЏ.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=switch label="Pump enable" state=on subtitle="Remote mode"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Switch(label = "Pump", checked = true))`
     */
    data class Switch(
        val label: String,
        val checked: Boolean,
        val subtitle: String? = null,
        val onColor: Color = Color(0xFF2ECC71),
        val offColor: Color = Color(0xFF54616C),
        val thumbColor: Color = Color.White,
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val labelColor: Color = Color.White,
        val subtitleColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec

    /**
     * Р С™Р В°РЎР‚РЎвЂљР С•РЎвЂЎР С”Р В° Р В°Р Р†Р В°РЎР‚Р С‘Р С‘ / Р С—РЎР‚Р ВµР Т‘РЎС“Р С—РЎР‚Р ВµР В¶Р Т‘Р ВµР Р…Р С‘РЎРЏ РЎРѓ Р С—Р В°Р В»Р С‘РЎвЂљРЎР‚Р С•Р в„– Р С—Р С• РЎС“РЎР‚Р С•Р Р†Р Р…РЎР‹ Р Р†Р В°Р В¶Р Р…Р С•РЎРѓРЎвЂљР С‘
     * Р С‘ Р Р…Р ВµР С•Р В±РЎРЏР В·Р В°РЎвЂљР ВµР В»РЎРЉР Р…Р С•Р в„– Р С•РЎвЂљР СР ВµРЎвЂљР С”Р С•Р в„– Р Р†РЎР‚Р ВµР СР ВµР Р…Р С‘.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=alarm-card title="Overheat" message="Motor 1: 92C" severity=critical time="12:41:03"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.AlarmCard(...))`
     */
    data class AlarmCard(
        val title: String,
        val message: String? = null,
        val severity: AlarmSeverity = AlarmSeverity.Warn,
        val timestamp: String? = null,
        val accentColor: Color,
        val backgroundColor: Color,
        val borderColor: Color,
        val titleColor: Color,
        val messageColor: Color,
        val metaColor: Color,
        val iconName: String? = null
    ) : ConsoleWidgetSpec

    /**
     * Р СљР С‘Р Р…Р С‘-Р С–РЎР‚Р В°РЎвЂћР С‘Р С” РЎвЂљРЎР‚Р ВµР Р…Р Т‘Р В° Р Р†Р Р…РЎС“РЎвЂљРЎР‚Р С‘ Р С•Р Т‘Р Р…Р С•Р в„– Р С”Р В°РЎР‚РЎвЂљР С•РЎвЂЎР С”Р С‘.
     * Р Р€Р Т‘Р С•Р В±Р ВµР Р… Р Т‘Р В»РЎРЏ РЎвЂљР ВµР СР С—Р ВµРЎР‚Р В°РЎвЂљРЎС“РЎР‚РЎвЂ№, RSSI, Р Р…Р В°Р С—РЎР‚РЎРЏР В¶Р ВµР Р…Р С‘РЎРЏ Р С‘ Р Т‘РЎР‚РЎС“Р С–Р С•Р в„– Р С”Р С•Р СР С—Р В°Р С”РЎвЂљР Р…Р С•Р в„– РЎвЂљР ВµР В»Р ВµР СР ВµРЎвЂљРЎР‚Р С‘Р С‘.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=sparkline label="Temp" values="21,22,22,23,24,23,25" min=18 max=28 color=#36C36B display="25C" points=on`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Sparkline(label = "Temp", values = listOf(21f, 22f, 25f)))`
     */
    data class Sparkline(
        val label: String? = null,
        val values: List<Float>,
        val min: Float? = null,
        val max: Float? = null,
        val text: String? = null,
        val lineColor: Color = Color(0xFF36C36B),
        val fillColor: Color = Color(0x2236C36B),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val labelColor: Color = Color.White,
        val valueColor: Color = Color(0xFFBDEFCF),
        val showDots: Boolean = false
    ) : ConsoleWidgetSpec

    /**
     * Р вЂњРЎР‚РЎС“Р С—Р С—Р В° РЎРѓРЎвЂљР С•Р В»Р В±Р С‘Р С”Р С•Р Р† Р Т‘Р В»РЎРЏ Р В±РЎвЂ№РЎРѓРЎвЂљРЎР‚Р С•Р С–Р С• РЎРѓРЎР‚Р В°Р Р†Р Р…Р ВµР Р…Р С‘РЎРЏ Р Р…Р ВµРЎРѓР С”Р С•Р В»РЎРЉР С”Р С‘РЎвЂ¦ Р С”Р В°Р Р…Р В°Р В»Р С•Р Р† Р С‘Р В»Р С‘ РЎС“РЎРѓРЎвЂљРЎР‚Р С•Р в„–РЎРѓРЎвЂљР Р†.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=bar-group title="Motors" labels="M1|M2|M3" values="20|45|80" max=100 colors="#36C36B|#4FC3F7|#FFB300"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.BarGroup(title = "Motors", labels = listOf("M1"), values = listOf(20f)))`
     */
    data class BarGroup(
        val title: String? = null,
        val labels: List<String>,
        val values: List<Float>,
        val max: Float? = null,
        val barColor: Color = Color(0xFFFFB300),
        val colors: List<Color> = emptyList(),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val labelColor: Color = Color(0xFFB5C0C8),
        val valueColor: Color = Color.White
    ) : ConsoleWidgetSpec

    /**
     * Р СџР С•Р В»РЎС“Р С”РЎР‚РЎС“Р С–Р В»РЎвЂ№Р в„– Р С‘Р Р…Р Т‘Р С‘Р С”Р В°РЎвЂљР С•РЎР‚ Р С•Р Т‘Р Р…Р С•Р С–Р С• Р В·Р Р…Р В°РЎвЂЎР ВµР Р…Р С‘РЎРЏ.
     * Р СџР С•Р Т‘РЎвЂ¦Р С•Р Т‘Р С‘РЎвЂљ Р Т‘Р В»РЎРЏ Р В·Р В°Р С–РЎР‚РЎС“Р В·Р С”Р С‘, РЎвЂљР ВµР СР С—Р ВµРЎР‚Р В°РЎвЂљРЎС“РЎР‚РЎвЂ№, Р Т‘Р В°Р Р†Р В»Р ВµР Р…Р С‘РЎРЏ, РЎРѓР С”Р С•РЎР‚Р С•РЎРѓРЎвЂљР С‘ Р С‘ Р Т‘РЎР‚РЎС“Р С–Р С‘РЎвЂ¦ Р С•Р Т‘Р С‘Р Р…Р С•РЎвЂЎР Р…РЎвЂ№РЎвЂ¦ Р СР ВµРЎвЂљРЎР‚Р С‘Р С”.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=gauge label="CPU" value=72 max=100 unit="%" color=#36C36B`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Gauge(label = "CPU", value = 72f, unit = "%"))`
     */
    data class Gauge(
        val label: String? = null,
        val value: Float,
        val max: Float = 100f,
        val unit: String? = null,
        val text: String? = null,
        val color: Color = Color(0xFF36C36B),
        val trackColor: Color = Color(0xFF1A242B),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val labelColor: Color = Color.White,
        val valueColor: Color = Color(0xFFBDEFCF)
    ) : ConsoleWidgetSpec

    /**
     * Р вЂ™Р С‘Р В·РЎС“Р В°Р В»РЎРЉР Р…Р С•Р Вµ РЎРѓР С•РЎРѓРЎвЂљР С•РЎРЏР Р…Р С‘Р Вµ Р В±Р В°РЎвЂљР В°РЎР‚Р ВµР С‘ РЎРѓ РЎС“РЎР‚Р С•Р Р†Р Р…Р ВµР С Р В·Р В°РЎР‚РЎРЏР Т‘Р В° Р С‘ Р Т‘Р С•Р С—Р С•Р В»Р Р…Р С‘РЎвЂљР ВµР В»РЎРЉР Р…Р С•Р в„– Р С—Р С•Р Т‘Р С—Р С‘РЎРѓРЎРЉРЎР‹.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=battery label="Battery A" value=78 max=100 charging=true voltage=4.08`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Battery(label = "Battery A", value = 78f, charging = true))`
     */
    data class Battery(
        val label: String? = null,
        val value: Float,
        val max: Float = 100f,
        val text: String? = null,
        val subtitle: String? = null,
        val charging: Boolean = false,
        val fillColor: Color? = null,
        val trackColor: Color = Color(0xFF1A242B),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val labelColor: Color = Color.White,
        val valueColor: Color = Color(0xFFBDEFCF),
        val subtitleColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec

    /**
     * Р В РЎРЏР Т‘ РЎРѓР Р†Р ВµРЎвЂљР С•Р Т‘Р С‘Р С•Р Т‘Р Р…РЎвЂ№РЎвЂ¦ Р С‘Р Р…Р Т‘Р С‘Р С”Р В°РЎвЂљР С•РЎР‚Р С•Р Р† РЎРѓР С•РЎРѓРЎвЂљР С•РЎРЏР Р…Р С‘РЎРЏ.
     * Р ТђР С•РЎР‚Р С•РЎв‚¬Р С• Р С—Р С•Р Т‘РЎвЂ¦Р С•Р Т‘Р С‘РЎвЂљ Р Т‘Р В»РЎРЏ Р С”Р В°Р Р…Р В°Р В»Р С•Р Р† РЎРѓР Р†РЎРЏР В·Р С‘, Р Т‘Р В°РЎвЂљРЎвЂЎР С‘Р С”Р С•Р Р†, РЎР‚Р ВµР В¶Р С‘Р СР С•Р Р† Р С‘ РЎвЂћР В»Р В°Р С–Р С•Р Р†.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=led-row title="Links" items="NET:#00E676|MQTT:#00E676|ERR:#FF5252|GPS:off"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.LedRow(title = "Links", items = listOf(LedRowItem("NET", Color.Green))))`
     */
    data class LedRow(
        val title: String? = null,
        val items: List<LedRowItem>,
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val labelColor: Color = Color(0xFFE3EEF5),
        val offColor: Color = Color(0xFF54616C)
    ) : ConsoleWidgetSpec

    /**
     * Р С™Р С•Р СР С—Р В°Р С”РЎвЂљР Р…Р В°РЎРЏ Р С”Р В°РЎР‚РЎвЂљР С•РЎвЂЎР С”Р В° Р С•Р Т‘Р Р…Р С•Р в„– Р СР ВµРЎвЂљРЎР‚Р С‘Р С”Р С‘ РЎРѓ Р В±Р С•Р В»РЎРЉРЎв‚¬Р С‘Р С РЎвЂЎР С‘РЎРѓР В»Р С•Р С, Р ВµР Т‘Р С‘Р Р…Р С‘РЎвЂ Р ВµР в„– Р С‘Р В·Р СР ВµРЎР‚Р ВµР Р…Р С‘РЎРЏ Р С‘ Р Т‘Р ВµР В»РЎРЉРЎвЂљР С•Р в„–.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=stats-card title="RPM" value=1450 unit="rpm" delta="+12" subtitle="Motor 1" accent=#36C36B`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.StatsCard(title = "RPM", value = "1450", unit = "rpm"))`
     */
    data class StatsCard(
        val title: String,
        val value: String,
        val unit: String? = null,
        val subtitle: String? = null,
        val delta: String? = null,
        val accentColor: Color = Color(0xFF36C36B),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color(0xFFB5C0C8),
        val valueColor: Color = Color.White,
        val subtitleColor: Color = Color(0xFF8FA1AD),
        val deltaColor: Color = Color(0xFF36C36B)
    ) : ConsoleWidgetSpec

    /**
     * Р РЋР ВµРЎвЂљР С”Р В° Р С”Р В»РЎР‹РЎвЂЎ-Р В·Р Р…Р В°РЎвЂЎР ВµР Р…Р С‘Р Вµ Р Т‘Р В»РЎРЏ Р С”Р С•Р СР С—Р В°Р С”РЎвЂљР Р…Р С•Р С–Р С• Р В±Р В»Р С•Р С”Р В° РЎвЂљР ВµР В»Р ВµР СР ВµРЎвЂљРЎР‚Р С‘Р С‘.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=kv-grid title="Motor 1" items="Voltage:24.3V|Current:1.8A|Temp:62C|State:READY" columns=2`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.KeyValueGrid(items = listOf(KeyValueGridItem("Voltage", "24.3V"))))`
     */
    data class KeyValueGrid(
        val title: String? = null,
        val items: List<KeyValueGridItem>,
        val columns: Int = 2,
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val keyColor: Color = Color(0xFF8FA1AD),
        val valueColor: Color = Color.White
    ) : ConsoleWidgetSpec

    /**
     * Р вЂР В°Р Р…Р С” Р С—Р С‘Р Р…Р С•Р Р† Р С‘ Р С”Р В°Р Р…Р В°Р В»Р С•Р Р† РЎРѓ Р В±РЎвЂ№РЎРѓРЎвЂљРЎР‚РЎвЂ№Р С Р Р†Р С‘Р В·РЎС“Р В°Р В»РЎРЉР Р…РЎвЂ№Р С РЎРѓРЎвЂљР В°РЎвЂљРЎС“РЎРѓР С•Р С.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=pin-bank title="GPIO" items="D1:on|D2:off|D3:warn|A0:adc|PWM1:pwm"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.PinBank(items = listOf(PinBankItem("D1", "ON", Color.Green))))`
     */
    data class PinBank(
        val title: String? = null,
        val items: List<PinBankItem>,
        val columns: Int = 3,
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val pinColor: Color = Color.White,
        val stateColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec

    /**
     * Р вЂєР ВµР Р…РЎвЂљР В° РЎРѓР С•Р В±РЎвЂ№РЎвЂљР С‘Р в„– Р С‘ РЎРЊРЎвЂљР В°Р С—Р С•Р Р† Р С—РЎР‚Р С•РЎвЂ Р ВµРЎРѓРЎРѓР В°.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=timeline title="Boot" items="12:01 Boot|12:03 WiFi connected|12:05 MQTT online"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.Timeline(items = listOf(TimelineItem("12:01", "Boot"))))`
     */
    data class Timeline(
        val title: String? = null,
        val items: List<TimelineItem>,
        val lineColor: Color = Color(0xFF36C36B),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val timeColor: Color = Color(0xFF8FA1AD),
        val textColor: Color = Color.White,
        val subtitleColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec

    /**
     * Р В Р В°РЎРѓРЎв‚¬Р С‘РЎР‚Р ВµР Р…Р Р…РЎвЂ№Р в„– Р В»Р С‘Р Р…Р ВµР в„–Р Р…РЎвЂ№Р в„– Р С–РЎР‚Р В°РЎвЂћР С‘Р С” РЎРѓ Р С—Р С•Р Т‘Р С—Р С‘РЎРѓРЎРЏР СР С‘ Р С‘ РЎв‚¬Р С”Р В°Р В»Р С•Р в„–.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=line-chart title="Voltage" values="24.1,24.2,24.0,24.3,24.4" labels="T1|T2|T3|T4|T5" min=23 max=25 color=#4FC3F7`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.LineChart(title = "Voltage", values = listOf(24.1f, 24.2f)))`
     */
    data class LineChart(
        val title: String? = null,
        val values: List<Float>,
        val labels: List<String> = emptyList(),
        val min: Float? = null,
        val max: Float? = null,
        val color: Color = Color(0xFF4FC3F7),
        val fillColor: Color = Color(0x224FC3F7),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val labelColor: Color = Color(0xFF8FA1AD),
        val axisColor: Color = Color(0xFF30404C),
        val showDots: Boolean = true
    ) : ConsoleWidgetSpec

    /**
     * Р СџР С•Р В±Р С‘РЎвЂљР С•Р Р†Р С•Р Вµ Р С—РЎР‚Р ВµР Т‘РЎРѓРЎвЂљР В°Р Р†Р В»Р ВµР Р…Р С‘Р Вµ РЎР‚Р ВµР С–Р С‘РЎРѓРЎвЂљРЎР‚Р В°, Р В±Р В°Р в„–РЎвЂљР В° Р С‘Р В»Р С‘ РЎРѓР В»Р С•Р Р†Р В°.
     * Р СџР С• РЎС“Р СР С•Р В»РЎвЂЎР В°Р Р…Р С‘РЎР‹ РЎС“Р Т‘Р С•Р В±Р Р…Р С• Р С—Р С•Р Т‘РЎвЂ¦Р С•Р Т‘Р С‘РЎвЂљ Р Т‘Р В»РЎРЏ `byte=8`, `short=16`, `word=32`.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=bitfield label="STATUS" value=0xA5 bits=8`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.BitField(label = "STATUS", value = 0xA5u, bitCount = 8))`
     */
    data class BitField(
        val label: String? = null,
        val value: ULong,
        val bitCount: Int = 8,
        val setColor: Color = Color(0xFF36C36B),
        val clearColor: Color = Color(0xFF202A31),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val labelColor: Color = Color.White,
        val indexColor: Color = Color(0xFF8FA1AD),
        val valueColor: Color = Color(0xFFE3EEF5)
    ) : ConsoleWidgetSpec

    /**
     * Р СћР В°Р В±Р В»Р С‘РЎвЂЎР Р…РЎвЂ№Р в„– Р Т‘Р В°Р СР С— Р В±Р В°Р в„–РЎвЂљР С•Р Р† Р Р† hex-РЎвЂћР С•РЎР‚Р СР В°РЎвЂљР Вµ РЎРѓ Р В°Р Т‘РЎР‚Р ВµРЎРѓР В°Р СР С‘ Р С‘ ASCII-Р С—РЎР‚Р ВµР Т‘РЎРѓРЎвЂљР В°Р Р†Р В»Р ВµР Р…Р С‘Р ВµР С.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=hex-dump title="RX Buffer" data="48 65 6C 6C 6F 20 57 6F 72 6C 64" width=8 addr=0x1000 ascii=on`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.HexDump(title = "RX Buffer", bytes = listOf(0x48, 0x65)))`
     */
    data class HexDump(
        val title: String? = null,
        val bytes: List<Int>,
        val bytesPerRow: Int = 8,
        val startAddress: Int = 0,
        val showAscii: Boolean = true,
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val addressColor: Color = Color(0xFF8FA1AD),
        val byteColor: Color = Color(0xFFE3EEF5),
        val asciiColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec

    /**
     * Р СћР В°Р В±Р В»Р С‘РЎвЂ Р В° РЎР‚Р ВµР С–Р С‘РЎРѓРЎвЂљРЎР‚Р С•Р Р† Р Р† РЎвЂћР С•РЎР‚Р СР В°РЎвЂљР Вµ `addr / value / desc`.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=register-table title="Holding Registers" rows="0000|0x1234|Status;0001|0x00A5|Flags;0002|0x03E8|Speed"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.RegisterTable(rows = listOf(RegisterTableRow("0000", "0x1234", "Status"))))`
     */
    data class RegisterTable(
        val title: String? = null,
        val rows: List<RegisterTableRow>,
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val headerBackgroundColor: Color = Color(0xFF1A2630),
        val headerTextColor: Color = Color.White,
        val addressColor: Color = Color(0xFF8FA1AD),
        val valueColor: Color = Color(0xFFE3EEF5),
        val descriptionColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec

    /**
     * Р В Р В°Р В·Р В±Р С•РЎР‚ Modbus-Р С”Р В°Р Т‘РЎР‚Р В° Р С—Р С• Р В±Р В°Р в„–РЎвЂљР В°Р С Р С‘ Р С—Р С•Р В»РЎРЏР С Р С—РЎР‚Р С•РЎвЂљР С•Р С”Р С•Р В»Р В°.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=modbus-frame direction=request preset=rtu data="01 03 00 10 00 02 C5 CE"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.ModbusFrame(bytes = listOf(0x01, 0x03), fields = listOf(ModbusFieldRow("0", "Addr", "01"))))`
     */
    data class ModbusFrame(
        val title: String? = null,
        val direction: ModbusDirection = ModbusDirection.Request,
        val bytes: List<Int>,
        val fields: List<ModbusFieldRow> = emptyList(),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val accentColor: Color,
        val byteColor: Color = Color(0xFFE3EEF5),
        val fieldNameColor: Color = Color.White,
        val fieldMetaColor: Color = Color(0xFF8FA1AD),
        val fieldDescriptionColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec

    /**
     * CAN-Р С”Р В°Р Т‘РЎР‚ РЎРѓ ID, РЎвЂћР В»Р В°Р С–Р В°Р СР С‘ Р С‘ Р С—Р С•Р В»Р ВµР В·Р Р…Р С•Р в„– Р Р…Р В°Р С–РЎР‚РЎС“Р В·Р С”Р С•Р в„–.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=can-frame title="Motor CAN" direction=rx id=0x18FF50E5 ext=true data="11 22 33 44 55 66 77 88" channel=can0`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.CanFrame(frameId = 0x18FF50E5, bytes = listOf(0x11, 0x22)))`
     */
    data class CanFrame(
        val title: String? = null,
        val direction: FrameDirection = FrameDirection.Rx,
        val frameId: Int,
        val bytes: List<Int> = emptyList(),
        val dlc: Int = bytes.size,
        val channel: String? = null,
        val extended: Boolean = false,
        val remote: Boolean = false,
        val fd: Boolean = false,
        val bitrateSwitch: Boolean = false,
        val fields: List<PacketFieldRow> = emptyList(),
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val accentColor: Color,
        val byteColor: Color = Color(0xFFE3EEF5),
        val metaColor: Color = Color(0xFF8FA1AD),
        val fieldNameColor: Color = Color.White,
        val fieldMetaColor: Color = Color(0xFF8FA1AD),
        val fieldDescriptionColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec

    /**
     * Р Р€Р Р…Р С‘Р Р†Р ВµРЎР‚РЎРѓР В°Р В»РЎРЉР Р…РЎвЂ№Р в„– Р В±Р С‘Р Р…Р В°РЎР‚Р Р…РЎвЂ№Р в„– Р С—Р В°Р С”Р ВµРЎвЂљ Р Т‘Р В»РЎРЏ UART Р С‘ Р Т‘РЎР‚РЎС“Р С–Р С‘РЎвЂ¦ Р С—РЎР‚Р С•Р С‘Р В·Р Р†Р С•Р В»РЎРЉР Р…РЎвЂ№РЎвЂ¦ Р С—РЎР‚Р С•РЎвЂљР С•Р С”Р С•Р В»Р С•Р Р†.
     *
     * Р РЋР ВµРЎвЂљР ВµР Р†Р В°РЎРЏ Р С”Р С•Р СР В°Р Р…Р Т‘Р В°:
     * `ui type=uart-frame title="UART RX" direction=rx channel=UART1 baud=115200 data="AA 55 10 02 01 02 34" fields="0-1|Sync|AA55|Preamble;2|Cmd|10|Command;3|Len|02|Payload length;4-5|Payload|0102|Data;6|CRC|34|Checksum"`
     *
     * Р вЂєР С•Р С”Р В°Р В»РЎРЉР Р…Р С•Р Вµ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р Вµ:
     * `console.printWidget(ConsoleWidgetSpec.PacketFrame(protocol = "UART", bytes = listOf(0xAA, 0x55)))`
     */
    data class PacketFrame(
        val title: String? = null,
        val protocol: String? = null,
        val direction: FrameDirection = FrameDirection.Rx,
        val bytes: List<Int>,
        val channel: String? = null,
        val baud: String? = null,
        val fields: List<PacketFieldRow> = emptyList(),
        val showAscii: Boolean = true,
        val backgroundColor: Color = Color(0xFF11171C),
        val borderColor: Color = Color(0xFF23303A),
        val titleColor: Color = Color.White,
        val accentColor: Color,
        val byteColor: Color = Color(0xFFE3EEF5),
        val metaColor: Color = Color(0xFF8FA1AD),
        val fieldNameColor: Color = Color.White,
        val fieldMetaColor: Color = Color(0xFF8FA1AD),
        val fieldDescriptionColor: Color = Color(0xFFB5C0C8)
    ) : ConsoleWidgetSpec
}

/**
 * Р С›Р Т‘Р С‘Р Р… Р С‘Р Р…Р Т‘Р С‘Р С”Р В°РЎвЂљР С•РЎР‚ Р Р†Р Р…РЎС“РЎвЂљРЎР‚Р С‘ [ConsoleWidgetSpec.LedRow].
 */
data class LedRowItem(
    val label: String,
    val color: Color,
    val isActive: Boolean = true
)

/**
 * Р В­Р В»Р ВµР СР ВµР Р…РЎвЂљ РЎРѓР ВµРЎвЂљР С”Р С‘ [ConsoleWidgetSpec.KeyValueGrid].
 */
data class KeyValueGridItem(
    val key: String,
    val value: String
)

/**
 * Р С›Р Т‘Р С‘Р Р… Р С—Р С‘Р Р… / Р С”Р В°Р Р…Р В°Р В» Р Р†Р Р…РЎС“РЎвЂљРЎР‚Р С‘ [ConsoleWidgetSpec.PinBank].
 */
data class PinBankItem(
    val pin: String,
    val state: String,
    val color: Color,
    val isActive: Boolean = true
)

/**
 * Р С›Р Т‘Р Р…Р С• РЎРѓР С•Р В±РЎвЂ№РЎвЂљР С‘Р Вµ Р Р†Р С• Р Р†РЎР‚Р ВµР СР ВµР Р…Р Р…Р С•Р в„– Р В»Р ВµР Р…РЎвЂљР Вµ [ConsoleWidgetSpec.Timeline].
 */
data class TimelineItem(
    val time: String? = null,
    val text: String,
    val subtitle: String? = null,
    val color: Color? = null
)

/**
 * Р РЋРЎвЂљРЎР‚Р С•Р С”Р В° РЎвЂљР В°Р В±Р В»Р С‘РЎвЂ РЎвЂ№ [ConsoleWidgetSpec.RegisterTable].
 */
data class RegisterTableRow(
    val address: String,
    val value: String,
    val description: String? = null
)

/**
 * Р С›Р Т‘Р Р…Р С• Р С—Р С•Р В»Р Вµ Р Р† [ConsoleWidgetSpec.ModbusFrame].
 */
data class ModbusFieldRow(
    val range: String,
    val name: String,
    val value: String? = null,
    val description: String? = null
)

/**
 * Р Р€Р Р…Р С‘Р Р†Р ВµРЎР‚РЎРѓР В°Р В»РЎРЉР Р…Р С•Р Вµ Р С—Р С•Р В»Р Вµ Р В±Р С‘Р Р…Р В°РЎР‚Р Р…Р С•Р С–Р С• Р С”Р В°Р Т‘РЎР‚Р В°.
 */
data class PacketFieldRow(
    val range: String,
    val name: String,
    val value: String? = null,
    val description: String? = null
)

/**
 * Р СњР В°Р С—РЎР‚Р В°Р Р†Р В»Р ВµР Р…Р С‘Р Вµ РЎвЂљРЎР‚Р В°Р Р…РЎРѓР С—Р С•РЎР‚РЎвЂљР Р…Р С•Р С–Р С• Р С”Р В°Р Т‘РЎР‚Р В°.
 */
enum class FrameDirection {
    Tx,
    Rx,
    Error
}

/**
 * Р СњР В°Р С—РЎР‚Р В°Р Р†Р В»Р ВµР Р…Р С‘Р Вµ Modbus-Р С”Р В°Р Т‘РЎР‚Р В°.
 */
enum class ModbusDirection {
    Request,
    Response,
    Error
}

/**
 * Р Р€РЎР‚Р С•Р Р†Р ВµР Р…РЎРЉ Р Р†Р В°Р В¶Р Р…Р С•РЎРѓРЎвЂљР С‘ Р Т‘Р В»РЎРЏ [ConsoleWidgetSpec.AlarmCard].
 */
enum class AlarmSeverity {
    Info,
    Warn,
    Error,
    Critical
}

/**
 * Р В Р В°Р В·Р В±Р С‘РЎР‚Р В°Р ВµРЎвЂљ Р В°РЎР‚Р С–РЎС“Р СР ВµР Р…РЎвЂљРЎвЂ№ Р С”Р С•Р СР В°Р Р…Р Т‘РЎвЂ№ РЎвЂћР С•РЎР‚Р СР В°РЎвЂљР В° `key=value` Р Р† [ConsoleWidgetSpec].
 *
 * Р СџР С•Р Т‘Р Т‘Р ВµРЎР‚Р В¶Р С‘Р Р†Р В°Р ВµР СРЎвЂ№Р Вµ РЎвЂљР С‘Р С—РЎвЂ№:
 * - `type=badge`
 * - `type=dot`
 * - `type=image`
 * - `type=panel`
 * - `type=progress`
 * - `type=2col`
 * - `type=table`
 * - `type=switch`
 * - `type=alarm-card`
 * - `type=sparkline`
 * - `type=bar-group`
 * - `type=gauge`
 * - `type=battery`
 * - `type=led-row`
 * - `type=stats-card`
 * - `type=kv-grid`
 * - `type=pin-bank`
 * - `type=timeline`
 * - `type=line-chart`
 * - `type=bitfield`
 * - `type=hex-dump`
 * - `type=register-table`
 * - `type=modbus-frame`
 * - `type=can-frame`
 * - `type=uart-frame`
 * - `type=packet-frame`
 *
 * Р вЂ”Р Р…Р В°РЎвЂЎР ВµР Р…Р С‘РЎРЏ РЎРѓ Р С—РЎР‚Р С•Р В±Р ВµР В»Р В°Р СР С‘ Р Р…РЎС“Р В¶Р Р…Р С• Р С•Р В±Р С•РЎР‚Р В°РЎвЂЎР С‘Р Р†Р В°РЎвЂљРЎРЉ Р Р† Р С”Р В°Р Р†РЎвЂ№РЎвЂЎР С”Р С‘.
 *
 * Р СџРЎР‚Р С‘Р СР ВµРЎР‚РЎвЂ№:
 * `ui type=panel title="Motor 1" value=READY subtitle="24.3V" accent=#36C36B`
 * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"`
 * `ui type=sparkline label="Temp" values="21,22,22,23,24,23,25" color=#36C36B`
 * `ui type=stats-card title="RPM" value=1450 unit="rpm" delta="+12"`
 * `ui type=bitfield label="STATUS" value=0xA5 bits=8`
 * `ui type=register-table title="Holding Registers" rows="0000|0x1234|Status"`
 * `ui type=can-frame id=0x18FF50E5 data="11 22 33 44"`
 */
object ConsoleWidgetProtocol {

    /**
     * Р В Р В°Р В·Р В±Р С‘РЎР‚Р В°Р ВµРЎвЂљ Р В°РЎР‚Р С–РЎС“Р СР ВµР Р…РЎвЂљРЎвЂ№ `key=value`, Р С—Р С•Р В»РЎС“РЎвЂЎР ВµР Р…Р Р…РЎвЂ№Р Вµ Р С‘Р В·
     * [com.example.terminalm3.network.NetCommandDecoder].
     *
     * Р СџРЎР‚Р С‘Р СР ВµРЎР‚:
     * `ConsoleWidgetProtocol.parse(listOf("type=badge", "text=READY", "st=ok"))`
     */
    fun parse(args: List<String>): Result<ConsoleWidgetSpec> = runCatching {
        val attributes = parseAttributes(args)
        val type = required(attributes, "type").lowercase()

        when (type) {
            "badge" -> parseBadge(attributes)
            "dot", "circle" -> parseDot(attributes)
            "image", "icon" -> parseImage(attributes)
            "panel", "card" -> parsePanel(attributes)
            "progress", "bar" -> parseProgressWidget(attributes)
            "2col", "twocol", "pair" -> parseTwoColumnWidget(attributes)
            "table", "grid" -> parseTableWidget(attributes)
            "switch", "toggle" -> parseSwitchWidget(attributes)
            "alarm-card", "alarm", "alert" -> parseAlarmCardWidget(attributes)
            "sparkline", "trend" -> parseSparklineWidget(attributes)
            "bar-group", "bars", "columns" -> parseBarGroupWidget(attributes)
            "gauge", "dial" -> parseGaugeWidget(attributes)
            "battery", "cell" -> parseBatteryWidget(attributes)
            "led-row", "leds", "status-row" -> parseLedRowWidget(attributes)
            "stats-card", "stat", "metric-card" -> parseStatsCardWidget(attributes)
            "kv-grid", "kv", "facts" -> parseKeyValueGridWidget(attributes)
            "pin-bank", "pins", "gpio" -> parsePinBankWidget(attributes)
            "timeline", "events", "log" -> parseTimelineWidget(attributes)
            "line-chart", "chart", "plot" -> parseLineChartWidget(attributes)
            "bitfield", "bits", "register" -> parseBitFieldWidget(attributes)
            "hex-dump", "hexdump", "dump" -> parseHexDumpWidget(attributes)
            "register-table", "registers", "reg-table" -> parseRegisterTableWidget(attributes)
            "modbus-frame", "modbus", "frame" -> parseModbusFrameWidget(attributes)
            "can-frame", "can" -> parseCanFrameWidget(attributes)
            "uart-frame", "uart" -> parsePacketFrameWidget(attributes, defaultProtocol = "UART")
            "packet-frame", "packet" -> parsePacketFrameWidget(attributes)
            else -> error("Unknown widget type: $type")
        }
    }

    /**
     * Пытается вытащить номер канала консоли из аргументов `ui` / `widget`.
     *
     * Поддерживаемые алиасы:
     * - `channel=5`
     * - `terminal=5`
     * - `term=5`
     * - `ch=5`
     *
     * Возвращает только числовой канал в диапазоне `0..3`.
     * Если значение не похоже на номер канала, вернет `null`.
     *
     * Важно: у frame-виджетов поле `channel` может означать имя шины или порта,
     * например `can0` или `UART1`. Для таких случаев безопаснее использовать
     * `terminal`, `term`, `ch` или transport-prefix `@N ` на уровне всей строки.
     */
    fun parseConsoleChannel(args: List<String>): Int? = runCatching {
        val attributes = parseAttributes(args)
        find(attributes, "channel", "terminal", "term", "ch")
            ?.toIntOrNull()
            ?.coerceIn(0, 3)
    }.getOrNull()

    /**
     * Пытается вытащить slot/index для позиционного виджета в канале.
     *
     * Поддерживаемые алиасы:
     * - `slot=0`
     * - `index=0`
     * - `pos=0`
     * - `position=0`
     */
    fun parseConsoleSlot(args: List<String>): Int? = runCatching {
        val attributes = parseAttributes(args)
        find(attributes, "slot", "index", "pos", "position")
            ?.toIntOrNull()
            ?.coerceAtLeast(0)
    }.getOrNull()

    private fun parseBadge(attributes: Map<String, String>): ConsoleWidgetSpec.Badge {
        val preset = parseBadgeStyle(find(attributes, "st", "style", "preset"))

        return ConsoleWidgetSpec.Badge(
            text = required(attributes, "text", "label", "title"),
            textColor = parseColor(
                find(attributes, "fg", "textColor", "color"),
                default = preset?.textColor ?: Color.White
            ),
            backgroundColor = parseColor(
                find(attributes, "bg", "background", "backgroundColor"),
                default = preset?.backgroundColor ?: Color(0xFF1F7A1F)
            ),
            fontSizeSp = parseInt(
                find(attributes, "size", "font", "fontSize"),
                default = 14,
                min = 10,
                max = 28
            )
        )
    }

    private fun parseBadgeStyle(style: String?): BadgeStylePreset? {
        if (style.isNullOrBlank()) return null

        return when (style.trim().lowercase()) {
            "ok", "ready", "success", "good", "green" -> BadgeStylePreset(
                backgroundColor = Color(0xFF1F7A1F),
                textColor = Color.White
            )

            "info", "blue" -> BadgeStylePreset(
                backgroundColor = Color(0xFF174A7A),
                textColor = Color.White
            )

            "warn", "warning", "amber", "yellow" -> BadgeStylePreset(
                backgroundColor = Color(0xFF7A5B12),
                textColor = Color.White
            )

            "error", "fail", "danger", "red" -> BadgeStylePreset(
                backgroundColor = Color(0xFF7A1F1F),
                textColor = Color.White
            )

            "critical", "alarm" -> BadgeStylePreset(
                backgroundColor = Color(0xFF5A1010),
                textColor = Color(0xFFFFE7E7)
            )

            "neutral", "default", "gray", "grey" -> BadgeStylePreset(
                backgroundColor = Color(0xFF33414D),
                textColor = Color.White
            )

            "dark", "muted" -> BadgeStylePreset(
                backgroundColor = Color(0xFF1D252C),
                textColor = Color(0xFFE3EEF5)
            )

            else -> null
        }
    }

    private fun parseDot(attributes: Map<String, String>): ConsoleWidgetSpec.Dot {
        return ConsoleWidgetSpec.Dot(
            color = parseColor(find(attributes, "color", "fg"), default = Color.Green),
            sizeDp = parseInt(
                find(attributes, "size", "diameter"),
                default = 14,
                min = 6,
                max = 64
            ),
            label = find(attributes, "text", "label", "title"),
            labelColor = parseColor(
                find(attributes, "labelColor", "textColor"),
                default = Color.White
            )
        )
    }

    private fun parseImage(attributes: Map<String, String>): ConsoleWidgetSpec.Image {
        return ConsoleWidgetSpec.Image(
            drawableName = required(attributes, "name", "icon", "image", "drawable"),
            sizeDp = parseInt(
                find(attributes, "size"),
                default = 32,
                min = 12,
                max = 128
            ),
            description = find(attributes, "desc", "description", "text")
        )
    }

    private fun parsePanel(attributes: Map<String, String>): ConsoleWidgetSpec.Panel {
        val title = find(attributes, "title", "text", "label")
        val value = find(attributes, "value", "status")

        require(!title.isNullOrBlank() || !value.isNullOrBlank()) {
            "Р вЂќР В»РЎРЏ panel Р Р…РЎС“Р В¶Р ВµР Р… РЎвЂ¦Р С•РЎвЂљРЎРЏ Р В±РЎвЂ№ title Р С‘Р В»Р С‘ value"
        }

        return ConsoleWidgetSpec.Panel(
            title = title ?: value.orEmpty(),
            value = value,
            subtitle = find(attributes, "subtitle", "sub", "caption"),
            accentColor = parseColor(find(attributes, "accent", "accentColor"), Color(0xFF4CAF50)),
            backgroundColor = parseColor(
                find(attributes, "bg", "background", "backgroundColor"),
                Color(0xFF11171C)
            ),
            borderColor = parseColor(
                find(attributes, "border", "borderColor"),
                Color(0xFF23303A)
            ),
            titleColor = parseColor(
                find(attributes, "titleColor", "fg", "color"),
                Color.White
            ),
            valueColor = parseColor(
                find(attributes, "valueColor"),
                Color.White
            ),
            subtitleColor = parseColor(
                find(attributes, "subtitleColor", "subColor"),
                Color(0xFFB5C0C8)
            ),
            iconName = find(attributes, "icon", "image")
        )
    }

    private fun parseAttributes(args: List<String>): Map<String, String> {
        require(args.isNotEmpty()) { "Р С™Р С•Р СР В°Р Р…Р Т‘Р В° Р Р†Р С‘Р Т‘Р В¶Р ВµРЎвЂљР В° Р Р…Р Вµ РЎРѓР С•Р Т‘Р ВµРЎР‚Р В¶Р С‘РЎвЂљ Р В°РЎР‚Р С–РЎС“Р СР ВµР Р…РЎвЂљР С•Р Р†" }

        val map = linkedMapOf<String, String>()

        args.forEach { rawArg ->
            val separatorIndex = rawArg.indexOf('=')
            require(separatorIndex > 0) {
                "Р С’РЎР‚Р С–РЎС“Р СР ВµР Р…РЎвЂљ '$rawArg' Р Т‘Р С•Р В»Р В¶Р ВµР Р… Р В±РЎвЂ№РЎвЂљРЎРЉ Р Р† РЎвЂћР С•РЎР‚Р СР В°РЎвЂљР Вµ key=value"
            }

            val key = rawArg.substring(0, separatorIndex).trim()
            val value = rawArg.substring(separatorIndex + 1).trim()

            require(key.isNotEmpty()) { "Р СџРЎС“РЎРѓРЎвЂљР С•Р Вµ Р С‘Р СРЎРЏ Р С—Р В°РЎР‚Р В°Р СР ВµРЎвЂљРЎР‚Р В° Р Р† '$rawArg'" }
            require(value.isNotEmpty()) { "Р СџРЎС“РЎРѓРЎвЂљР С•Р Вµ Р В·Р Р…Р В°РЎвЂЎР ВµР Р…Р С‘Р Вµ Р С—Р В°РЎР‚Р В°Р СР ВµРЎвЂљРЎР‚Р В° '$key'" }

            map[key] = value
        }

        return map
    }

    private fun find(attributes: Map<String, String>, vararg keys: String): String? {
        return keys.firstNotNullOfOrNull { key -> attributes[key] }
    }

    private fun required(attributes: Map<String, String>, vararg keys: String): String {
        return find(attributes, *keys)
            ?.takeIf { it.isNotBlank() }
            ?: error("Р СњР Вµ Р Р…Р В°Р в„–Р Т‘Р ВµР Р… Р С•Р В±РЎРЏР В·Р В°РЎвЂљР ВµР В»РЎРЉР Р…РЎвЂ№Р в„– Р С—Р В°РЎР‚Р В°Р СР ВµРЎвЂљРЎР‚: ${keys.joinToString(" / ")}")
    }

    private fun parseInt(value: String?, default: Int, min: Int, max: Int): Int {
        return value
            ?.toIntOrNull()
            ?.coerceIn(min, max)
            ?: default
    }

    private fun parseColor(value: String?, default: Color): Color {
        if (value.isNullOrBlank()) return default

        val normalized = value.trim().lowercase()
        namedColors[normalized]?.let { return it }
        return parseWidgetHexColorOrNull(normalized) ?: default
    }

    private val namedColors = mapOf(
        "black" to Color.Black,
        "white" to Color.White,
        "red" to Color.Red,
        "green" to Color.Green,
        "blue" to Color.Blue,
        "yellow" to Color.Yellow,
        "cyan" to Color.Cyan,
        "magenta" to Color.Magenta,
        "gray" to Color.Gray,
        "orange" to Color(0xFFFF9800)
    )
}

private data class BadgeStylePreset(
    val backgroundColor: Color,
    val textColor: Color
)

/**
 * Отрисовывает один уже разобранный [ConsoleWidgetSpec] как реальный Compose-элемент.
 *
 * Это единая точка роутинга: по типу spec выбирается нужный `...ConsoleWidget(...)`
 * из папки `console/widgets`.
 */
@Composable
fun ConsoleWidget(spec: ConsoleWidgetSpec) {
    when (spec) {
        is ConsoleWidgetSpec.Badge -> BadgeConsoleWidget(spec)
        is ConsoleWidgetSpec.BarGroup -> BarGroupConsoleWidget(spec)
        is ConsoleWidgetSpec.Battery -> BatteryConsoleWidget(spec)
        is ConsoleWidgetSpec.BitField -> BitFieldConsoleWidget(spec)
        is ConsoleWidgetSpec.CanFrame -> CanFrameConsoleWidget(spec)
        is ConsoleWidgetSpec.Dot -> DotConsoleWidget(spec)
        is ConsoleWidgetSpec.Gauge -> GaugeConsoleWidget(spec)
        is ConsoleWidgetSpec.HexDump -> HexDumpConsoleWidget(spec)
        is ConsoleWidgetSpec.Image -> ImageConsoleWidget(spec)
        is ConsoleWidgetSpec.KeyValueGrid -> KeyValueGridConsoleWidget(spec)
        is ConsoleWidgetSpec.LedRow -> LedRowConsoleWidget(spec)
        is ConsoleWidgetSpec.LineChart -> LineChartConsoleWidget(spec)
        is ConsoleWidgetSpec.ModbusFrame -> ModbusFrameConsoleWidget(spec)
        is ConsoleWidgetSpec.PacketFrame -> PacketFrameConsoleWidget(spec)
        is ConsoleWidgetSpec.Panel -> PanelConsoleWidget(spec)
        is ConsoleWidgetSpec.PinBank -> PinBankConsoleWidget(spec)
        is ConsoleWidgetSpec.Progress -> ProgressConsoleWidget(spec)
        is ConsoleWidgetSpec.RegisterTable -> RegisterTableConsoleWidget(spec)
        is ConsoleWidgetSpec.Sparkline -> SparklineConsoleWidget(spec)
        is ConsoleWidgetSpec.StatsCard -> StatsCardConsoleWidget(spec)
        is ConsoleWidgetSpec.TwoColumn -> TwoColumnConsoleWidget(spec)
        is ConsoleWidgetSpec.Table -> TableConsoleWidget(spec)
        is ConsoleWidgetSpec.Switch -> SwitchConsoleWidget(spec)
        is ConsoleWidgetSpec.AlarmCard -> AlarmCardConsoleWidget(spec)
        is ConsoleWidgetSpec.Timeline -> TimelineConsoleWidget(spec)
    }
}

/**
 * Добавляет виджет в конец указанного канала консоли.
 *
 * Если [channelId] не передан, используется текущий [Console.defaultOutputChannel].
 */
fun Console.printWidget(
    spec: ConsoleWidgetSpec,
    channelId: Int = defaultOutputChannel
) {
    printComposable(channelId = channelId) {
        ConsoleWidget(spec)
    }
}

/**
 * Вставляет или обновляет виджет в стабильном slot/index канала.
 */
fun Console.printWidgetAt(
    slotIndex: Int,
    spec: ConsoleWidgetSpec,
    channelId: Int = defaultOutputChannel
) {
    printComposableAt(slotIndex = slotIndex, channelId = channelId) {
        ConsoleWidget(spec)
    }
}

/**
 * Вставляет виджет после конкретной входящей сетевой строки.
 *
 * Используется, когда команда пришла из сети и виджет должен визуально стоять
 * сразу под строкой, которая эту команду породила. Если строка еще не завершена,
 * вставка будет отложена до [Console.completeRemoteLine].
 */
fun Console.printWidgetAfterRemoteLine(
    remoteLineId: Long,
    spec: ConsoleWidgetSpec,
    channelId: Int = defaultOutputChannel
) {
    printComposableAfterRemoteLine(remoteLineId, channelId = channelId) {
        ConsoleWidget(spec)
    }
}
