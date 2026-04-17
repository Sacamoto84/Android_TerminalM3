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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Data-only description of a console widget that can be rendered by Compose.
 *
 * The microcontroller sends a textual command, the app parses it into one of
 * these specs, and then [ConsoleWidget] renders the actual UI element.
 */
sealed interface ConsoleWidgetSpec {
    /**
     * Compact rounded label for short statuses such as READY / OK / FAIL.
     *
     * Network command:
     * `ui type=badge text="READY" bg=#1F7A1F fg=#FFFFFF size=14`
     *
     * Local usage:
     * `console.printWidget(ConsoleWidgetSpec.Badge(text = "READY"))`
     */
    data class Badge(
        val text: String,
        val textColor: Color = Color.White,
        val backgroundColor: Color = Color(0xFF1F7A1F),
        val fontSizeSp: Int = 14
    ) : ConsoleWidgetSpec

    /**
     * Circular indicator that can optionally show a label on the right.
     *
     * Network command:
     * `ui type=dot color=#00FF66 size=16 label="Link active"`
     *
     * Local usage:
     * `console.printWidget(ConsoleWidgetSpec.Dot(color = Color.Green, label = "Link"))`
     */
    data class Dot(
        val color: Color = Color.Green,
        val sizeDp: Int = 14,
        val label: String? = null,
        val labelColor: Color = Color.White
    ) : ConsoleWidgetSpec

    /**
     * Drawable resource from `res/drawable`.
     *
     * Network command:
     * `ui type=image name=info size=32`
     *
     * Local usage:
     * `console.printWidget(ConsoleWidgetSpec.Image(drawableName = "info"))`
     */
    data class Image(
        val drawableName: String,
        val sizeDp: Int = 32,
        val description: String? = null
    ) : ConsoleWidgetSpec

    /**
     * Rich status card with title, optional subtitle, value and icon.
     *
     * Network command:
     * `ui type=panel title="Motor 1" value=READY subtitle="24.3V" accent=#36C36B icon=info`
     *
     * Local usage:
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
     * Progress bar card for battery, loading, production percent, etc.
     *
     * Network command:
     * `ui type=progress label="Battery" value=72 max=100 fill=#36C36B display="72%"`
     *
     * Local usage:
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
     * Two-column row for key/value style telemetry.
     *
     * Network command:
     * `ui type=2col left="Voltage" right="24.3V"`
     *
     * Local usage:
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
     * Table with optional headers and multiple rows.
     *
     * Network command:
     * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"`
     *
     * Local usage:
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
     * Visual on/off switch. It is display-only and does not handle clicks.
     *
     * Network command:
     * `ui type=switch label="Pump enable" state=on subtitle="Remote mode"`
     *
     * Local usage:
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
     * Alarm / alert card with severity palette and optional timestamp.
     *
     * Network command:
     * `ui type=alarm-card title="Overheat" message="Motor 1: 92C" severity=critical time="12:41:03"`
     *
     * Local usage:
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
}

/**
 * Severity level for [ConsoleWidgetSpec.AlarmCard].
 */
enum class AlarmSeverity {
    Info,
    Warn,
    Error,
    Critical
}

/**
 * Parses key=value command arguments into a [ConsoleWidgetSpec].
 *
 * Supported types:
 * - `type=badge`
 * - `type=dot`
 * - `type=image`
 * - `type=panel`
 * - `type=progress`
 * - `type=2col`
 * - `type=table`
 * - `type=switch`
 * - `type=alarm-card`
 *
 * Values with spaces should be wrapped in quotes.
 *
 * Examples:
 * `ui type=panel title="Motor 1" value=READY subtitle="24.3V" accent=#36C36B`
 * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"`
 */
object ConsoleWidgetProtocol {

    /**
     * Parses `key=value` command arguments received from
     * [com.example.terminalm3.network.NetCommandDecoder].
     *
     * Example:
     * `ConsoleWidgetProtocol.parse(listOf("type=badge", "text=READY", "bg=#1F7A1F"))`
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
            else -> error("Unknown widget type: $type")
        }
    }

    private fun parseBadge(attributes: Map<String, String>): ConsoleWidgetSpec.Badge {
        return ConsoleWidgetSpec.Badge(
            text = required(attributes, "text", "label", "title"),
            textColor = parseColor(
                find(attributes, "fg", "textColor", "color"),
                default = Color.White
            ),
            backgroundColor = parseColor(
                find(attributes, "bg", "background", "backgroundColor"),
                default = Color(0xFF1F7A1F)
            ),
            fontSizeSp = parseInt(
                find(attributes, "size", "font", "fontSize"),
                default = 14,
                min = 10,
                max = 28
            )
        )
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
            "Для panel нужен хотя бы title или value"
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
        require(args.isNotEmpty()) { "Команда виджета не содержит аргументов" }

        val map = linkedMapOf<String, String>()

        args.forEach { rawArg ->
            val separatorIndex = rawArg.indexOf('=')
            require(separatorIndex > 0) {
                "Аргумент '$rawArg' должен быть в формате key=value"
            }

            val key = rawArg.substring(0, separatorIndex).trim()
            val value = rawArg.substring(separatorIndex + 1).trim()

            require(key.isNotEmpty()) { "Пустое имя параметра в '$rawArg'" }
            require(value.isNotEmpty()) { "Пустое значение параметра '$key'" }

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
            ?: error("Не найден обязательный параметр: ${keys.joinToString(" / ")}")
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

        val hex = normalized.removePrefix("#")

        return when (hex.length) {
            6 -> Color((0xFF000000 or hex.toLong(16)).toULong())
            8 -> Color(hex.toLong(16).toULong())
            else -> default
        }
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

/**
 * Renders a parsed widget specification as a real Compose element.
 *
 * Example:
 * `ConsoleWidget(ConsoleWidgetSpec.Badge(text = "READY"))`
 */
@Composable
fun ConsoleWidget(spec: ConsoleWidgetSpec) {
    when (spec) {
        is ConsoleWidgetSpec.Badge -> BadgeWidget(spec)
        is ConsoleWidgetSpec.Dot -> DotWidget(spec)
        is ConsoleWidgetSpec.Image -> ImageWidget(spec)
        is ConsoleWidgetSpec.Panel -> PanelWidget(spec)
        is ConsoleWidgetSpec.Progress -> ProgressWidget(spec)
        is ConsoleWidgetSpec.TwoColumn -> TwoColumnWidget(spec)
        is ConsoleWidgetSpec.Table -> TableWidget(spec)
        is ConsoleWidgetSpec.Switch -> SwitchWidget(spec)
        is ConsoleWidgetSpec.AlarmCard -> AlarmCardWidget(spec)
    }
}

/**
 * Adds a data-driven widget to the end of the console.
 *
 * Example:
 * `console.printWidget(ConsoleWidgetSpec.Progress(label = "Battery", value = 72f))`
 */
fun Console.printWidget(spec: ConsoleWidgetSpec) {
    printComposable {
        ConsoleWidget(spec)
    }
}

/**
 * Adds a data-driven widget after a specific remote line.
 *
 * Example:
 * `console.printWidgetAfterRemoteLine(lineId, ConsoleWidgetSpec.Dot(label = "Link"))`
 */
fun Console.printWidgetAfterRemoteLine(remoteLineId: Long, spec: ConsoleWidgetSpec) {
    printComposableAfterRemoteLine(remoteLineId) {
        ConsoleWidget(spec)
    }
}

/**
 * Compose renderer for [ConsoleWidgetSpec.Badge].
 */
@Composable
private fun BadgeWidget(spec: ConsoleWidgetSpec.Badge) {
    Text(
        text = spec.text,
        color = spec.textColor,
        fontSize = spec.fontSizeSp.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(spec.backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

/**
 * Compose renderer for [ConsoleWidgetSpec.Dot].
 */
@Composable
private fun DotWidget(spec: ConsoleWidgetSpec.Dot) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(spec.sizeDp.dp)
                .clip(CircleShape)
                .background(spec.color)
        )

        spec.label?.takeIf { it.isNotBlank() }?.let { label ->
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = spec.labelColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Compose renderer for [ConsoleWidgetSpec.Image].
 */
@Composable
private fun ImageWidget(spec: ConsoleWidgetSpec.Image) {
    val context = LocalContext.current
    val drawableId = remember(spec.drawableName) {
        context.resources.getIdentifier(spec.drawableName, "drawable", context.packageName)
    }

    if (drawableId == 0) {
        Text(
            text = "Drawable not found: ${spec.drawableName}",
            color = Color(0xFFFF8A80),
            fontSize = 13.sp
        )
        return
    }

    Image(
        painter = painterResource(drawableId),
        contentDescription = spec.description,
        modifier = Modifier.size(spec.sizeDp.dp)
    )
}

/**
 * Compose renderer for [ConsoleWidgetSpec.Panel].
 */
@Composable
private fun PanelWidget(spec: ConsoleWidgetSpec.Panel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(
                width = 1.dp,
                color = spec.borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(spec.accentColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        spec.iconName?.takeIf { it.isNotBlank() }?.let { iconName ->
            val context = LocalContext.current
            val drawableId = remember(iconName) {
                context.resources.getIdentifier(iconName, "drawable", context.packageName)
            }

            if (drawableId != 0) {
                Image(
                    painter = painterResource(drawableId),
                    contentDescription = spec.title,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = spec.title,
                color = spec.titleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
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

        spec.value?.takeIf { it.isNotBlank() }?.let { value ->
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = value,
                color = spec.valueColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


