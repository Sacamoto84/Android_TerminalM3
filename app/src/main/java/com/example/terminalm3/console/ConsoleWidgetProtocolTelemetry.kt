package com.example.terminalm3.console

import androidx.compose.ui.graphics.Color
import java.util.Locale

/**
 * Parses `type=sparkline`.
 *
 * Example network command:
 * `ui type=sparkline label="Temp" values="21,22,22,23,24,23,25" min=18 max=28 color=#36C36B display="25C" points=on`
 */
fun parseSparklineWidget(attributes: Map<String, String>): ConsoleWidgetSpec.Sparkline {
    val values = parseWidgetFloatList(requiredAttribute(attributes, "values", "points", "data"))
    require(values.isNotEmpty()) { "sparkline requires values" }

    val lineColor = parseWidgetColor(
        findAttribute(attributes, "color", "line", "accent"),
        Color(0xFF36C36B)
    )

    return ConsoleWidgetSpec.Sparkline(
        label = findAttribute(attributes, "label", "title"),
        values = values,
        min = findAttribute(attributes, "min")?.let { parseWidgetFloat(it, values.minOrNull() ?: 0f) },
        max = findAttribute(attributes, "max")?.let { parseWidgetFloat(it, values.maxOrNull() ?: 100f) },
        text = findAttribute(attributes, "display", "valueText", "caption")
            ?: formatWidgetNumber(values.last()),
        lineColor = lineColor,
        fillColor = parseWidgetColor(findAttribute(attributes, "fill"), lineColor.copy(alpha = 0.18f)),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor", "fg"), Color.White),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor"), Color(0xFFBDEFCF)),
        showDots = parseWidgetBoolean(findAttribute(attributes, "points", "dots", "showPoints"), false)
    )
}

/**
 * Parses `type=bar-group`.
 *
 * Example network command:
 * `ui type=bar-group title="Motors" labels="M1|M2|M3" values="20|45|80" max=100 colors="#36C36B|#4FC3F7|#FFB300"`
 */
fun parseBarGroupWidget(attributes: Map<String, String>): ConsoleWidgetSpec.BarGroup {
    val values = parseWidgetFloatList(requiredAttribute(attributes, "values", "data"))
    require(values.isNotEmpty()) { "bar-group requires values" }

    val rawLabels = splitWidgetList(findAttribute(attributes, "labels", "names"), '|')
    val labels = when {
        rawLabels.isEmpty() -> List(values.size) { index -> "${index + 1}" }
        rawLabels.size >= values.size -> rawLabels.take(values.size)
        else -> rawLabels + List(values.size - rawLabels.size) { "" }
    }

    return ConsoleWidgetSpec.BarGroup(
        title = findAttribute(attributes, "title", "label"),
        labels = labels,
        values = values,
        max = findAttribute(attributes, "max")?.let { parseWidgetFloat(it, values.maxOrNull() ?: 100f) },
        barColor = parseWidgetColor(findAttribute(attributes, "color", "barColor", "accent"), Color(0xFFFFB300)),
        colors = parseWidgetColorList(findAttribute(attributes, "colors")),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor"), Color(0xFFB5C0C8)),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor"), Color.White)
    )
}

/**
 * Parses `type=gauge`.
 *
 * Example network command:
 * `ui type=gauge label="CPU" value=72 max=100 unit="%" color=#36C36B`
 */
fun parseGaugeWidget(attributes: Map<String, String>): ConsoleWidgetSpec.Gauge {
    val value = parseWidgetFloat(requiredAttribute(attributes, "value"), 0f)
    val max = parseWidgetFloat(findAttribute(attributes, "max"), 100f).coerceAtLeast(0.0001f)
    val unit = findAttribute(attributes, "unit")

    return ConsoleWidgetSpec.Gauge(
        label = findAttribute(attributes, "label", "title"),
        value = value,
        max = max,
        unit = unit,
        text = findAttribute(attributes, "display", "valueText", "caption")
            ?: buildString {
                append(formatWidgetNumber(value))
                unit?.takeIf { it.isNotBlank() }?.let { append(it) }
            },
        color = parseWidgetColor(findAttribute(attributes, "color", "accent"), Color(0xFF36C36B)),
        trackColor = parseWidgetColor(findAttribute(attributes, "track"), Color(0xFF1A242B)),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor", "fg"), Color.White),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor"), Color(0xFFBDEFCF))
    )
}

/**
 * Parses `type=battery`.
 *
 * Example network command:
 * `ui type=battery label="Battery A" value=78 max=100 charging=true voltage=4.08`
 */
fun parseBatteryWidget(attributes: Map<String, String>): ConsoleWidgetSpec.Battery {
    val value = parseWidgetFloat(requiredAttribute(attributes, "value", "level"), 0f)
    val max = parseWidgetFloat(findAttribute(attributes, "max"), 100f).coerceAtLeast(0.0001f)
    val voltage = findAttribute(attributes, "voltage")
    val subtitle = findAttribute(attributes, "subtitle", "meta", "caption")
        ?: voltage?.let(::formatVoltageText)

    return ConsoleWidgetSpec.Battery(
        label = findAttribute(attributes, "label", "title"),
        value = value,
        max = max,
        text = findAttribute(attributes, "display", "valueText")
            ?: "${((value / max).coerceIn(0f, 1f) * 100f).toInt()}%",
        subtitle = subtitle,
        charging = parseWidgetBoolean(findAttribute(attributes, "charging", "charge"), false),
        fillColor = parseWidgetColorOrNull(findAttribute(attributes, "fill", "color", "accent")),
        trackColor = parseWidgetColor(findAttribute(attributes, "track"), Color(0xFF1A242B)),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor", "fg"), Color.White),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor"), Color(0xFFBDEFCF)),
        subtitleColor = parseWidgetColor(findAttribute(attributes, "subtitleColor"), Color(0xFFB5C0C8))
    )
}

/**
 * Parses `type=led-row`.
 *
 * Example network command:
 * `ui type=led-row title="Links" items="NET:#00E676|MQTT:#00E676|ERR:#FF5252|GPS:off"`
 */
fun parseLedRowWidget(attributes: Map<String, String>): ConsoleWidgetSpec.LedRow {
    val offColor = parseWidgetColor(findAttribute(attributes, "offColor"), Color(0xFF54616C))
    val items = splitWidgetList(requiredAttribute(attributes, "items", "states", "leds"), '|')
        .map { token -> parseLedRowItem(token, offColor) }
        .filter { it.label.isNotBlank() }

    require(items.isNotEmpty()) { "led-row requires items" }

    return ConsoleWidgetSpec.LedRow(
        title = findAttribute(attributes, "title", "label"),
        items = items,
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor"), Color(0xFFE3EEF5)),
        offColor = offColor
    )
}

internal fun parseWidgetFloatList(value: String?): List<Float> {
    return value
        ?.split('|', ';', ',')
        ?.mapNotNull { token ->
            token.trim()
                .takeIf { it.isNotEmpty() }
                ?.replace(',', '.')
                ?.toFloatOrNull()
        }
        .orEmpty()
}

internal fun parseWidgetColorList(value: String?): List<Color> {
    return value
        ?.split('|', ';', ',')
        ?.mapNotNull { token -> parseWidgetColorOrNull(token.trim()) }
        .orEmpty()
}

internal fun parseWidgetColorOrNull(value: String?): Color? {
    if (value.isNullOrBlank()) return null

    val normalized = value.trim().lowercase()
    widgetTelemetryNamedColors[normalized]?.let { return it }
    return parseWidgetHexColorOrNull(normalized)
}

internal fun formatWidgetNumber(value: Float): String {
    return if (value == value.toInt().toFloat()) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
            .trimEnd('0')
            .trimEnd('.')
    }
}

private fun formatVoltageText(value: String): String {
    val trimmed = value.trim()
    return if (trimmed.any(Char::isLetter)) trimmed else "${trimmed}V"
}

private fun parseLedRowItem(token: String, offColor: Color): LedRowItem {
    val separatorIndex = token.indexOf(':')
    if (separatorIndex <= 0) {
        return LedRowItem(
            label = token.trim(),
            color = Color(0xFF00E676),
            isActive = true
        )
    }

    val label = token.substring(0, separatorIndex).trim()
    val state = token.substring(separatorIndex + 1).trim()
    val normalized = state.lowercase()

    return when (normalized) {
        "0", "false", "off", "no", "disabled", "inactive" -> LedRowItem(label, offColor, false)
        "1", "true", "on", "yes", "enabled", "active", "ok" -> LedRowItem(label, Color(0xFF00E676), true)
        "warn", "warning" -> LedRowItem(label, Color(0xFFFFC107), true)
        "error", "err", "alarm", "critical" -> LedRowItem(label, Color(0xFFFF5252), true)
        else -> LedRowItem(
            label = label,
            color = parseWidgetColorOrNull(state) ?: Color(0xFF00E676),
            isActive = true
        )
    }
}

private val widgetTelemetryNamedColors = mapOf(
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
