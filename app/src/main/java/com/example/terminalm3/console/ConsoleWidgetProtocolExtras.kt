package com.example.terminalm3.console

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

/**
 * Parses `type=progress`.
 *
 * Example network command:
 * `ui type=progress label="Battery" value=72 max=100 fill=#36C36B display="72%"`
 */
fun parseProgressWidget(attributes: Map<String, String>): ConsoleWidgetSpec.Progress {
    val value = parseWidgetFloat(requiredAttribute(attributes, "value", "progress"), 0f)
    val max = parseWidgetFloat(findAttribute(attributes, "max"), 100f).coerceAtLeast(0.0001f)
    val percentText = ((value / max).coerceIn(0f, 1f) * 100f).roundToInt()

    return ConsoleWidgetSpec.Progress(
        label = findAttribute(attributes, "label", "title", "text"),
        value = value,
        max = max,
        text = findAttribute(attributes, "display", "valueText", "caption") ?: "$percentText%",
        fillColor = parseWidgetColor(findAttribute(attributes, "fill", "color", "accent"), Color(0xFF3AC972)),
        trackColor = parseWidgetColor(findAttribute(attributes, "track"), Color(0xFF1A242B)),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor", "fg"), Color.White),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor"), Color(0xFFBDEFCF))
    )
}

/**
 * Parses `type=2col`.
 *
 * Example network command:
 * `ui type=2col left="Voltage" right="24.3V"`
 */
fun parseTwoColumnWidget(attributes: Map<String, String>): ConsoleWidgetSpec.TwoColumn {
    return ConsoleWidgetSpec.TwoColumn(
        left = requiredAttribute(attributes, "left", "title", "label", "key"),
        right = requiredAttribute(attributes, "right", "value"),
        leftColor = parseWidgetColor(findAttribute(attributes, "leftColor", "labelColor"), Color(0xFFB5C0C8)),
        rightColor = parseWidgetColor(findAttribute(attributes, "rightColor", "valueColor", "fg"), Color.White),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF10151A)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF202C35))
    )
}

/**
 * Parses `type=table`.
 *
 * Example network command:
 * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"`
 */
fun parseTableWidget(attributes: Map<String, String>): ConsoleWidgetSpec.Table {
    val headers = splitWidgetList(findAttribute(attributes, "headers", "header"), '|')
    val rows = splitWidgetList(requiredAttribute(attributes, "rows", "data"), ';')
        .map { row -> splitWidgetList(row, '|') }
        .filter { row -> row.isNotEmpty() }

    require(rows.isNotEmpty()) { "table requires rows" }

    return ConsoleWidgetSpec.Table(
        headers = headers,
        rows = rows,
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF10151A)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF25323B)),
        headerBackgroundColor = parseWidgetColor(findAttribute(attributes, "headerBg"), Color(0xFF1A2630)),
        headerTextColor = parseWidgetColor(findAttribute(attributes, "headerColor"), Color.White),
        cellTextColor = parseWidgetColor(findAttribute(attributes, "cellColor", "fg"), Color(0xFFE3EEF5))
    )
}

/**
 * Parses `type=switch`.
 *
 * Example network command:
 * `ui type=switch label="Pump enable" state=on subtitle="Remote mode"`
 */
fun parseSwitchWidget(attributes: Map<String, String>): ConsoleWidgetSpec.Switch {
    return ConsoleWidgetSpec.Switch(
        label = requiredAttribute(attributes, "label", "title", "text"),
        checked = parseWidgetBoolean(findAttribute(attributes, "checked", "value", "state"), false),
        subtitle = findAttribute(attributes, "subtitle", "sub", "caption"),
        onColor = parseWidgetColor(findAttribute(attributes, "onColor", "accent"), Color(0xFF2ECC71)),
        offColor = parseWidgetColor(findAttribute(attributes, "offColor", "off"), Color(0xFF54616C)),
        thumbColor = parseWidgetColor(findAttribute(attributes, "thumb", "thumbColor"), Color.White),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor", "fg"), Color.White),
        subtitleColor = parseWidgetColor(findAttribute(attributes, "subtitleColor"), Color(0xFFB5C0C8))
    )
}

/**
 * Parses `type=alarm-card`.
 *
 * Example network command:
 * `ui type=alarm-card title="Overheat" message="Motor 1: 92C" severity=critical time="12:41:03"`
 */
fun parseAlarmCardWidget(attributes: Map<String, String>): ConsoleWidgetSpec.AlarmCard {
    val severity = parseWidgetSeverity(findAttribute(attributes, "severity", "level", "state"))
    val palette = widgetAlarmPalette(severity)

    return ConsoleWidgetSpec.AlarmCard(
        title = requiredAttribute(attributes, "title", "label", "text"),
        message = findAttribute(attributes, "message", "subtitle", "body", "desc"),
        severity = severity,
        timestamp = findAttribute(attributes, "time", "timestamp", "meta"),
        accentColor = parseWidgetColor(findAttribute(attributes, "accent"), palette.accentColor),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), palette.backgroundColor),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), palette.borderColor),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), palette.titleColor),
        messageColor = parseWidgetColor(findAttribute(attributes, "messageColor"), palette.messageColor),
        metaColor = parseWidgetColor(findAttribute(attributes, "metaColor"), palette.metaColor),
        iconName = findAttribute(attributes, "icon", "image")
    )
}

internal fun findAttribute(attributes: Map<String, String>, vararg keys: String): String? {
    return keys.firstNotNullOfOrNull { key -> attributes[key] }
}

internal fun requiredAttribute(attributes: Map<String, String>, vararg keys: String): String {
    return findAttribute(attributes, *keys)
        ?.takeIf { it.isNotBlank() }
        ?: error("Missing required parameter: ${keys.joinToString(" / ")}")
}

internal fun splitWidgetList(value: String?, delimiter: Char): List<String> {
    return value
        ?.split(delimiter)
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        .orEmpty()
}

internal fun parseWidgetFloat(value: String?, default: Float): Float {
    return value
        ?.replace(',', '.')
        ?.toFloatOrNull()
        ?: default
}

internal fun parseWidgetBoolean(value: String?, default: Boolean): Boolean {
    return when (value?.trim()?.lowercase()) {
        "1", "true", "on", "yes", "checked", "enabled" -> true
        "0", "false", "off", "no", "unchecked", "disabled" -> false
        null -> default
        else -> default
    }
}

internal fun parseWidgetSeverity(value: String?): AlarmSeverity {
    return when (value?.trim()?.lowercase()) {
        "info", "notice" -> AlarmSeverity.Info
        "warn", "warning" -> AlarmSeverity.Warn
        "error", "err", "danger" -> AlarmSeverity.Error
        "critical", "fatal", "panic" -> AlarmSeverity.Critical
        else -> AlarmSeverity.Warn
    }
}

internal fun parseWidgetColor(value: String?, default: Color): Color {
    if (value.isNullOrBlank()) return default

    val normalized = value.trim().lowercase()
    widgetNamedColors[normalized]?.let { return it }

    val hex = normalized.removePrefix("#")

    return when (hex.length) {
        6 -> Color((0xFF000000 or hex.toLong(16)).toULong())
        8 -> Color(hex.toLong(16).toULong())
        else -> default
    }
}

internal fun widgetAlarmPalette(severity: AlarmSeverity): WidgetAlarmPalette {
    return when (severity) {
        AlarmSeverity.Info -> WidgetAlarmPalette(
            accentColor = Color(0xFF4FC3F7),
            backgroundColor = Color(0xFF0F1922),
            borderColor = Color(0xFF1C3A4E),
            titleColor = Color.White,
            messageColor = Color(0xFFD6EEF9),
            metaColor = Color(0xFF8FC5DD)
        )

        AlarmSeverity.Warn -> WidgetAlarmPalette(
            accentColor = Color(0xFFFFC107),
            backgroundColor = Color(0xFF211A0B),
            borderColor = Color(0xFF5C4710),
            titleColor = Color.White,
            messageColor = Color(0xFFFFF0C2),
            metaColor = Color(0xFFE4C970)
        )

        AlarmSeverity.Error -> WidgetAlarmPalette(
            accentColor = Color(0xFFFF7043),
            backgroundColor = Color(0xFF26120D),
            borderColor = Color(0xFF6A2A19),
            titleColor = Color.White,
            messageColor = Color(0xFFFFDDD2),
            metaColor = Color(0xFFF1A58F)
        )

        AlarmSeverity.Critical -> WidgetAlarmPalette(
            accentColor = Color(0xFFFF1744),
            backgroundColor = Color(0xFF280812),
            borderColor = Color(0xFF7B1730),
            titleColor = Color.White,
            messageColor = Color(0xFFFFD8E1),
            metaColor = Color(0xFFFF9AB0)
        )
    }
}

private val widgetNamedColors = mapOf(
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

internal data class WidgetAlarmPalette(
    val accentColor: Color,
    val backgroundColor: Color,
    val borderColor: Color,
    val titleColor: Color,
    val messageColor: Color,
    val metaColor: Color
)
