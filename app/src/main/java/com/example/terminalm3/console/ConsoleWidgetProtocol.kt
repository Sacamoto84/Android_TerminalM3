package com.example.terminalm3.console

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.terminalm3.console.widgets.AlarmCardConsoleWidget
import com.example.terminalm3.console.widgets.BadgeConsoleWidget
import com.example.terminalm3.console.widgets.BarGroupConsoleWidget
import com.example.terminalm3.console.widgets.BatteryConsoleWidget
import com.example.terminalm3.console.widgets.DotConsoleWidget
import com.example.terminalm3.console.widgets.GaugeConsoleWidget
import com.example.terminalm3.console.widgets.ImageConsoleWidget
import com.example.terminalm3.console.widgets.LedRowConsoleWidget
import com.example.terminalm3.console.widgets.PanelConsoleWidget
import com.example.terminalm3.console.widgets.ProgressConsoleWidget
import com.example.terminalm3.console.widgets.SparklineConsoleWidget
import com.example.terminalm3.console.widgets.SwitchConsoleWidget
import com.example.terminalm3.console.widgets.TableConsoleWidget
import com.example.terminalm3.console.widgets.TwoColumnConsoleWidget

/**
 * Описание консольного виджета в виде данных, которое потом можно отрисовать через Compose.
 *
 * Микроконтроллер отправляет текстовую команду, приложение разбирает ее в один
 * из этих `spec`-объектов, а затем [ConsoleWidget] передает отрисовку
 * соответствующему Compose-виджету из пакета `console/widgets`.
 */
sealed interface ConsoleWidgetSpec {
    /**
     * Компактная округлая плашка для коротких статусов, например READY / OK / FAIL.
     *
     * Сетевая команда:
     * `ui type=badge text="READY" bg=#1F7A1F fg=#FFFFFF size=14`
     *
     * Локальное использование:
     * `console.printWidget(ConsoleWidgetSpec.Badge(text = "READY"))`
     */
    data class Badge(
        val text: String,
        val textColor: Color = Color.White,
        val backgroundColor: Color = Color(0xFF1F7A1F),
        val fontSizeSp: Int = 14
    ) : ConsoleWidgetSpec

    /**
     * Круглый индикатор, который при желании может показывать подпись справа.
     *
     * Сетевая команда:
     * `ui type=dot color=#00FF66 size=16 label="Link active"`
     *
     * Локальное использование:
     * `console.printWidget(ConsoleWidgetSpec.Dot(color = Color.Green, label = "Link"))`
     */
    data class Dot(
        val color: Color = Color.Green,
        val sizeDp: Int = 14,
        val label: String? = null,
        val labelColor: Color = Color.White
    ) : ConsoleWidgetSpec

    /**
     * Drawable-ресурс из `res/drawable`.
     *
     * Сетевая команда:
     * `ui type=image name=info size=32`
     *
     * Локальное использование:
     * `console.printWidget(ConsoleWidgetSpec.Image(drawableName = "info"))`
     */
    data class Image(
        val drawableName: String,
        val sizeDp: Int = 32,
        val description: String? = null
    ) : ConsoleWidgetSpec

    /**
     * Расширенная статусная карточка с заголовком, необязательным подзаголовком,
     * значением и иконкой.
     *
     * Сетевая команда:
     * `ui type=panel title="Motor 1" value=READY subtitle="24.3V" accent=#36C36B icon=info`
     *
     * Локальное использование:
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
     * Карточка с полосой прогресса для батареи, загрузки, процента выполнения и т.д.
     *
     * Сетевая команда:
     * `ui type=progress label="Battery" value=72 max=100 fill=#36C36B display="72%"`
     *
     * Локальное использование:
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
     * Двухколоночная строка для телеметрии формата `ключ -> значение`.
     *
     * Сетевая команда:
     * `ui type=2col left="Voltage" right="24.3V"`
     *
     * Локальное использование:
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
     * Таблица с необязательными заголовками и несколькими строками.
     *
     * Сетевая команда:
     * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"`
     *
     * Локальное использование:
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
     * Визуальный переключатель ON/OFF.
     * Он используется только для отображения и не обрабатывает нажатия.
     *
     * Сетевая команда:
     * `ui type=switch label="Pump enable" state=on subtitle="Remote mode"`
     *
     * Локальное использование:
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
     * Карточка аварии / предупреждения с палитрой по уровню важности
     * и необязательной отметкой времени.
     *
     * Сетевая команда:
     * `ui type=alarm-card title="Overheat" message="Motor 1: 92C" severity=critical time="12:41:03"`
     *
     * Локальное использование:
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
     * Мини-график тренда внутри одной карточки.
     * Удобен для температуры, RSSI, напряжения и другой компактной телеметрии.
     *
     * Сетевая команда:
     * `ui type=sparkline label="Temp" values="21,22,22,23,24,23,25" min=18 max=28 color=#36C36B display="25C" points=on`
     *
     * Локальное использование:
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
     * Группа столбиков для быстрого сравнения нескольких каналов или устройств.
     *
     * Сетевая команда:
     * `ui type=bar-group title="Motors" labels="M1|M2|M3" values="20|45|80" max=100 colors="#36C36B|#4FC3F7|#FFB300"`
     *
     * Локальное использование:
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
     * Полукруглый индикатор одного значения.
     * Подходит для загрузки, температуры, давления, скорости и других одиночных метрик.
     *
     * Сетевая команда:
     * `ui type=gauge label="CPU" value=72 max=100 unit="%" color=#36C36B`
     *
     * Локальное использование:
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
     * Визуальное состояние батареи с уровнем заряда и дополнительной подписью.
     *
     * Сетевая команда:
     * `ui type=battery label="Battery A" value=78 max=100 charging=true voltage=4.08`
     *
     * Локальное использование:
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
     * Ряд светодиодных индикаторов состояния.
     * Хорошо подходит для каналов связи, датчиков, режимов и флагов.
     *
     * Сетевая команда:
     * `ui type=led-row title="Links" items="NET:#00E676|MQTT:#00E676|ERR:#FF5252|GPS:off"`
     *
     * Локальное использование:
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
}

/**
 * Один индикатор внутри [ConsoleWidgetSpec.LedRow].
 */
data class LedRowItem(
    val label: String,
    val color: Color,
    val isActive: Boolean = true
)

/**
 * Уровень важности для [ConsoleWidgetSpec.AlarmCard].
 */
enum class AlarmSeverity {
    Info,
    Warn,
    Error,
    Critical
}

/**
 * Разбирает аргументы команды формата `key=value` в [ConsoleWidgetSpec].
 *
 * Поддерживаемые типы:
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
 *
 * Значения с пробелами нужно оборачивать в кавычки.
 *
 * Примеры:
 * `ui type=panel title="Motor 1" value=READY subtitle="24.3V" accent=#36C36B`
 * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9"`
 * `ui type=sparkline label="Temp" values="21,22,22,23,24,23,25" color=#36C36B`
 */
object ConsoleWidgetProtocol {

    /**
     * Разбирает аргументы `key=value`, полученные из
     * [com.example.terminalm3.network.NetCommandDecoder].
     *
     * Пример:
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
            "sparkline", "trend" -> parseSparklineWidget(attributes)
            "bar-group", "bars", "columns" -> parseBarGroupWidget(attributes)
            "gauge", "dial" -> parseGaugeWidget(attributes)
            "battery", "cell" -> parseBatteryWidget(attributes)
            "led-row", "leds", "status-row" -> parseLedRowWidget(attributes)
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
 * Отрисовывает разобранное описание виджета как реальный Compose-элемент.
 *
 * Пример:
 * `ConsoleWidget(ConsoleWidgetSpec.Badge(text = "READY"))`
 */
@Composable
fun ConsoleWidget(spec: ConsoleWidgetSpec) {
    when (spec) {
        is ConsoleWidgetSpec.Badge -> BadgeConsoleWidget(spec)
        is ConsoleWidgetSpec.BarGroup -> BarGroupConsoleWidget(spec)
        is ConsoleWidgetSpec.Battery -> BatteryConsoleWidget(spec)
        is ConsoleWidgetSpec.Dot -> DotConsoleWidget(spec)
        is ConsoleWidgetSpec.Gauge -> GaugeConsoleWidget(spec)
        is ConsoleWidgetSpec.Image -> ImageConsoleWidget(spec)
        is ConsoleWidgetSpec.LedRow -> LedRowConsoleWidget(spec)
        is ConsoleWidgetSpec.Panel -> PanelConsoleWidget(spec)
        is ConsoleWidgetSpec.Progress -> ProgressConsoleWidget(spec)
        is ConsoleWidgetSpec.Sparkline -> SparklineConsoleWidget(spec)
        is ConsoleWidgetSpec.TwoColumn -> TwoColumnConsoleWidget(spec)
        is ConsoleWidgetSpec.Table -> TableConsoleWidget(spec)
        is ConsoleWidgetSpec.Switch -> SwitchConsoleWidget(spec)
        is ConsoleWidgetSpec.AlarmCard -> AlarmCardConsoleWidget(spec)
    }
}

/**
 * Добавляет виджет, описанный данными, в конец консоли.
 *
 * Пример:
 * `console.printWidget(ConsoleWidgetSpec.Progress(label = "Battery", value = 72f))`
 */
fun Console.printWidget(spec: ConsoleWidgetSpec) {
    printComposable {
        ConsoleWidget(spec)
    }
}

/**
 * Добавляет виджет после указанной удаленной строки консоли.
 *
 * Пример:
 * `console.printWidgetAfterRemoteLine(lineId, ConsoleWidgetSpec.Dot(label = "Link"))`
 */
fun Console.printWidgetAfterRemoteLine(remoteLineId: Long, spec: ConsoleWidgetSpec) {
    printComposableAfterRemoteLine(remoteLineId) {
        ConsoleWidget(spec)
    }
}
