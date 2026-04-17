package com.example.terminalm3.console

import androidx.compose.ui.graphics.Color

/**
 * Parses `type=stats-card`.
 *
 * Example network command:
 * `ui type=stats-card title="RPM" value=1450 unit="rpm" delta="+12" subtitle="Motor 1" accent=#36C36B`
 */
fun parseStatsCardWidget(attributes: Map<String, String>): ConsoleWidgetSpec.StatsCard {
    val accentColor = parseWidgetColor(
        findAttribute(attributes, "accent", "color", "deltaColor"),
        Color(0xFF36C36B)
    )
    val delta = findAttribute(attributes, "delta", "trend", "change")
    val defaultDeltaColor = when {
        delta.isNullOrBlank() -> accentColor
        delta.trim().startsWith("-") -> Color(0xFFFF7043)
        delta.trim().startsWith("+") -> accentColor
        else -> Color(0xFFB5C0C8)
    }

    return ConsoleWidgetSpec.StatsCard(
        title = requiredAttribute(attributes, "title", "label"),
        value = requiredAttribute(attributes, "value", "text", "number"),
        unit = findAttribute(attributes, "unit"),
        subtitle = findAttribute(attributes, "subtitle", "sub", "caption"),
        delta = delta,
        accentColor = accentColor,
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor"), Color(0xFFB5C0C8)),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor", "fg"), Color.White),
        subtitleColor = parseWidgetColor(findAttribute(attributes, "subtitleColor"), Color(0xFF8FA1AD)),
        deltaColor = parseWidgetColor(findAttribute(attributes, "deltaColor"), defaultDeltaColor)
    )
}

/**
 * Parses `type=kv-grid`.
 *
 * Example network command:
 * `ui type=kv-grid title="Motor 1" items="Voltage:24.3V|Current:1.8A|Temp:62C|State:READY" columns=2`
 */
fun parseKeyValueGridWidget(attributes: Map<String, String>): ConsoleWidgetSpec.KeyValueGrid {
    val items = splitWidgetList(requiredAttribute(attributes, "items", "pairs", "rows"), '|')
        .map(::parseKeyValueGridItem)
        .filter { item -> item.key.isNotBlank() || item.value.isNotBlank() }

    require(items.isNotEmpty()) { "kv-grid requires items" }

    return ConsoleWidgetSpec.KeyValueGrid(
        title = findAttribute(attributes, "title", "label"),
        items = items,
        columns = findAttribute(attributes, "columns", "cols")
            ?.toIntOrNull()
            ?.coerceIn(1, 3)
            ?: 2,
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        keyColor = parseWidgetColor(findAttribute(attributes, "keyColor"), Color(0xFF8FA1AD)),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor"), Color.White)
    )
}

/**
 * Parses `type=pin-bank`.
 *
 * Example network command:
 * `ui type=pin-bank title="GPIO" items="D1:on|D2:off|D3:warn|A0:adc|PWM1:pwm"`
 */
fun parsePinBankWidget(attributes: Map<String, String>): ConsoleWidgetSpec.PinBank {
    val items = splitWidgetList(requiredAttribute(attributes, "items", "states", "pins"), '|')
        .map(::parsePinBankItem)
        .filter { item -> item.pin.isNotBlank() }

    require(items.isNotEmpty()) { "pin-bank requires items" }

    return ConsoleWidgetSpec.PinBank(
        title = findAttribute(attributes, "title", "label"),
        items = items,
        columns = findAttribute(attributes, "columns", "cols")
            ?.toIntOrNull()
            ?.coerceIn(1, 4)
            ?: 3,
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        pinColor = parseWidgetColor(findAttribute(attributes, "pinColor"), Color.White),
        stateColor = parseWidgetColor(findAttribute(attributes, "stateColor"), Color(0xFFB5C0C8))
    )
}

/**
 * Parses `type=timeline`.
 *
 * Example network command:
 * `ui type=timeline title="Boot" items="12:01 Boot|12:03 WiFi connected|12:05 MQTT online"`
 */
fun parseTimelineWidget(attributes: Map<String, String>): ConsoleWidgetSpec.Timeline {
    val items = splitWidgetList(requiredAttribute(attributes, "items", "events", "rows"), '|')
        .map(::parseTimelineItem)
        .filter { item -> item.text.isNotBlank() }

    require(items.isNotEmpty()) { "timeline requires items" }

    return ConsoleWidgetSpec.Timeline(
        title = findAttribute(attributes, "title", "label"),
        items = items,
        lineColor = parseWidgetColor(findAttribute(attributes, "line", "color", "accent"), Color(0xFF36C36B)),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        timeColor = parseWidgetColor(findAttribute(attributes, "timeColor"), Color(0xFF8FA1AD)),
        textColor = parseWidgetColor(findAttribute(attributes, "textColor"), Color.White),
        subtitleColor = parseWidgetColor(findAttribute(attributes, "subtitleColor"), Color(0xFFB5C0C8))
    )
}

/**
 * Parses `type=line-chart`.
 *
 * Example network command:
 * `ui type=line-chart title="Voltage" values="24.1,24.2,24.0,24.3,24.4" labels="T1|T2|T3|T4|T5" min=23 max=25 color=#4FC3F7`
 */
fun parseLineChartWidget(attributes: Map<String, String>): ConsoleWidgetSpec.LineChart {
    val values = parseWidgetFloatList(requiredAttribute(attributes, "values", "points", "data"))
    require(values.isNotEmpty()) { "line-chart requires values" }

    return ConsoleWidgetSpec.LineChart(
        title = findAttribute(attributes, "title", "label"),
        values = values,
        labels = splitWidgetList(findAttribute(attributes, "labels", "x"), '|'),
        min = findAttribute(attributes, "min")?.let { parseWidgetFloat(it, values.minOrNull() ?: 0f) },
        max = findAttribute(attributes, "max")?.let { parseWidgetFloat(it, values.maxOrNull() ?: 100f) },
        color = parseWidgetColor(findAttribute(attributes, "color", "line", "accent"), Color(0xFF4FC3F7)),
        fillColor = parseWidgetColor(findAttribute(attributes, "fill"), Color(0x224FC3F7)),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor"), Color(0xFF8FA1AD)),
        axisColor = parseWidgetColor(findAttribute(attributes, "axisColor"), Color(0xFF30404C)),
        showDots = parseWidgetBoolean(findAttribute(attributes, "points", "dots", "showPoints"), true)
    )
}

private fun parseKeyValueGridItem(token: String): KeyValueGridItem {
    val separatorIndex = token.indexOf(':').takeIf { it > 0 } ?: token.indexOf('=')
    if (separatorIndex <= 0) {
        return KeyValueGridItem(
            key = token.trim(),
            value = ""
        )
    }

    return KeyValueGridItem(
        key = token.substring(0, separatorIndex).trim(),
        value = token.substring(separatorIndex + 1).trim()
    )
}

private fun parsePinBankItem(token: String): PinBankItem {
    val separatorIndex = token.indexOf(':')
    if (separatorIndex <= 0) {
        return PinBankItem(
            pin = token.trim(),
            state = "ON",
            color = Color(0xFF36C36B),
            isActive = true
        )
    }

    val pin = token.substring(0, separatorIndex).trim()
    val rawState = token.substring(separatorIndex + 1).trim()
    parseWidgetColorOrNull(rawState)?.let { color ->
        return PinBankItem(
            pin = pin,
            state = "CUSTOM",
            color = color,
            isActive = true
        )
    }

    return when (rawState.lowercase()) {
        "on", "1", "high", "enabled" -> PinBankItem(pin, "ON", Color(0xFF36C36B), true)
        "off", "0", "low", "disabled" -> PinBankItem(pin, "OFF", Color(0xFF54616C), false)
        "warn", "warning" -> PinBankItem(pin, "WARN", Color(0xFFFFC107), true)
        "error", "err", "alarm", "critical" -> PinBankItem(pin, "ERR", Color(0xFFFF5252), true)
        "adc", "analog" -> PinBankItem(pin, "ADC", Color(0xFF4FC3F7), true)
        "pwm" -> PinBankItem(pin, "PWM", Color(0xFFFFB300), true)
        "in", "input" -> PinBankItem(pin, "IN", Color(0xFF9FA8DA), true)
        "out", "output" -> PinBankItem(pin, "OUT", Color(0xFF26C6DA), true)
        else -> PinBankItem(pin, rawState.uppercase(), Color(0xFF36C36B), true)
    }
}

private fun parseTimelineItem(token: String): TimelineItem {
    if ('~' in token) {
        val parts = token.split('~')
            .map { it.trim() }
        val time = parts.getOrNull(0)?.takeIf { it.isNotBlank() }
        val text = parts.getOrNull(1).orEmpty()
        val subtitle = parts.getOrNull(2)?.takeIf { it.isNotBlank() }
        val color = parseWidgetColorOrNull(parts.getOrNull(3))
        return TimelineItem(time = time, text = text, subtitle = subtitle, color = color)
    }

    val firstSpace = token.indexOf(' ')
    return if (firstSpace > 0) {
        TimelineItem(
            time = token.substring(0, firstSpace).trim(),
            text = token.substring(firstSpace + 1).trim()
        )
    } else {
        TimelineItem(text = token.trim())
    }
}
