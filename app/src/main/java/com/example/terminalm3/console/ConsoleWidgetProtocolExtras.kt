package com.example.terminalm3.console

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * Compose renderer for [ConsoleWidgetSpec.Progress].
 */
@Composable
fun ProgressWidget(spec: ConsoleWidgetSpec.Progress) {
    val fraction = (spec.value / spec.max).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        if (!spec.label.isNullOrBlank() || !spec.text.isNullOrBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                spec.label?.takeIf { it.isNotBlank() }?.let { label ->
                    Text(
                        text = label,
                        color = spec.labelColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                } ?: Spacer(modifier = Modifier.weight(1f))

                spec.text?.takeIf { it.isNotBlank() }?.let { valueText ->
                    Text(
                        text = valueText,
                        color = spec.valueColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(spec.trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(spec.fillColor)
            )
        }
    }
}

/**
 * Compose renderer for [ConsoleWidgetSpec.TwoColumn].
 */
@Composable
fun TwoColumnWidget(spec: ConsoleWidgetSpec.TwoColumn) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = spec.left,
            color = spec.leftColor,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = spec.right,
            color = spec.rightColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

/**
 * Compose renderer for [ConsoleWidgetSpec.Table].
 */
@Composable
fun TableWidget(spec: ConsoleWidgetSpec.Table) {
    val columnCount = maxOf(
        spec.headers.size,
        spec.rows.maxOfOrNull { row -> row.size } ?: 0
    ).coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        if (spec.headers.isNotEmpty()) {
            TableRowWidget(
                cells = spec.headers.normalizeWidgetRow(columnCount),
                textColor = spec.headerTextColor,
                backgroundColor = spec.headerBackgroundColor,
                isHeader = true
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        spec.rows.forEachIndexed { index, row ->
            val rowBackground = if (index % 2 == 0) {
                spec.backgroundColor.copy(alpha = 0.30f)
            } else {
                spec.backgroundColor.copy(alpha = 0.12f)
            }

            TableRowWidget(
                cells = row.normalizeWidgetRow(columnCount),
                textColor = spec.cellTextColor,
                backgroundColor = rowBackground,
                isHeader = false
            )

            if (index != spec.rows.lastIndex) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/**
 * Compose renderer for [ConsoleWidgetSpec.Switch].
 */
@Composable
fun SwitchWidget(spec: ConsoleWidgetSpec.Switch) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = spec.label,
                color = spec.labelColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            spec.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = spec.subtitleColor,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .width(46.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (spec.checked) spec.onColor else spec.offColor)
                .padding(3.dp),
            contentAlignment = if (spec.checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(spec.thumbColor)
            )
        }
    }
}

/**
 * Compose renderer for [ConsoleWidgetSpec.AlarmCard].
 */
@Composable
fun AlarmCardWidget(spec: ConsoleWidgetSpec.AlarmCard) {
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
        WidgetProtocolIcon(spec.iconName, spec.title, 26)

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

@Composable
private fun TableRowWidget(
    cells: List<String>,
    textColor: Color,
    backgroundColor: Color,
    isHeader: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        cells.forEachIndexed { index, cell ->
            Text(
                text = cell,
                color = textColor,
                fontSize = if (isHeader) 13.sp else 12.sp,
                fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
                textAlign = if (index == 0) TextAlign.Start else TextAlign.Center
            )
        }
    }
}

@Composable
private fun WidgetProtocolIcon(iconName: String?, contentDescription: String, sizeDp: Int) {
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
private fun rememberWidgetDrawableId(drawableName: String): Int {
    val context = LocalContext.current
    return remember(drawableName) {
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)
    }
}

private fun findAttribute(attributes: Map<String, String>, vararg keys: String): String? {
    return keys.firstNotNullOfOrNull { key -> attributes[key] }
}

private fun requiredAttribute(attributes: Map<String, String>, vararg keys: String): String {
    return findAttribute(attributes, *keys)
        ?.takeIf { it.isNotBlank() }
        ?: error("Missing required parameter: ${keys.joinToString(" / ")}")
}

private fun splitWidgetList(value: String?, delimiter: Char): List<String> {
    return value
        ?.split(delimiter)
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        .orEmpty()
}

private fun parseWidgetFloat(value: String?, default: Float): Float {
    return value
        ?.replace(',', '.')
        ?.toFloatOrNull()
        ?: default
}

private fun parseWidgetBoolean(value: String?, default: Boolean): Boolean {
    return when (value?.trim()?.lowercase()) {
        "1", "true", "on", "yes", "checked", "enabled" -> true
        "0", "false", "off", "no", "unchecked", "disabled" -> false
        null -> default
        else -> default
    }
}

private fun parseWidgetSeverity(value: String?): AlarmSeverity {
    return when (value?.trim()?.lowercase()) {
        "info", "notice" -> AlarmSeverity.Info
        "warn", "warning" -> AlarmSeverity.Warn
        "error", "err", "danger" -> AlarmSeverity.Error
        "critical", "fatal", "panic" -> AlarmSeverity.Critical
        else -> AlarmSeverity.Warn
    }
}

private fun parseWidgetColor(value: String?, default: Color): Color {
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

private fun widgetAlarmPalette(severity: AlarmSeverity): WidgetAlarmPalette {
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

private fun List<String>.normalizeWidgetRow(columnCount: Int): List<String> {
    return if (size >= columnCount) {
        take(columnCount)
    } else {
        this + List(columnCount - size) { "" }
    }
}

private fun severityLabel(severity: AlarmSeverity): String {
    return when (severity) {
        AlarmSeverity.Info -> "INFO"
        AlarmSeverity.Warn -> "WARN"
        AlarmSeverity.Error -> "ERROR"
        AlarmSeverity.Critical -> "CRITICAL"
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

private data class WidgetAlarmPalette(
    val accentColor: Color,
    val backgroundColor: Color,
    val borderColor: Color,
    val titleColor: Color,
    val messageColor: Color,
    val metaColor: Color
)
