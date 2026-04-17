package com.example.terminalm3.console

import androidx.compose.ui.graphics.Color

/**
 * Parses `type=register-table`.
 *
 * Example network command:
 * `ui type=register-table title="Holding Registers" rows="0000|0x1234|Status;0001|0x00A5|Flags;0002|0x03E8|Speed"`
 */
fun parseRegisterTableWidget(attributes: Map<String, String>): ConsoleWidgetSpec.RegisterTable {
    val rows = splitWidgetList(requiredAttribute(attributes, "rows", "registers", "data"), ';')
        .map(::parseRegisterTableRow)
        .filter { row -> row.address.isNotBlank() || row.value.isNotBlank() || !row.description.isNullOrBlank() }

    require(rows.isNotEmpty()) { "register-table requires rows" }

    return ConsoleWidgetSpec.RegisterTable(
        title = findAttribute(attributes, "title", "label"),
        rows = rows,
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        headerBackgroundColor = parseWidgetColor(findAttribute(attributes, "headerBg"), Color(0xFF1A2630)),
        headerTextColor = parseWidgetColor(findAttribute(attributes, "headerColor"), Color.White),
        addressColor = parseWidgetColor(findAttribute(attributes, "addressColor"), Color(0xFF8FA1AD)),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor"), Color(0xFFE3EEF5)),
        descriptionColor = parseWidgetColor(findAttribute(attributes, "descriptionColor", "descColor"), Color(0xFFB5C0C8))
    )
}

/**
 * Parses `type=modbus-frame`.
 *
 * Example network command:
 * `ui type=modbus-frame title="Read Holding Registers" direction=request data="01 03 00 10 00 02 C5 CE" fields="0|Addr|01|Slave ID;1|Func|03|Read Holding;2-3|Start|0010|Address;4-5|Count|0002|Registers;6-7|CRC|C5CE|CRC16"`
 */
fun parseModbusFrameWidget(attributes: Map<String, String>): ConsoleWidgetSpec.ModbusFrame {
    val bytes = parseWidgetHexByteList(requiredAttribute(attributes, "data", "bytes", "frame"))
    require(bytes.isNotEmpty()) { "modbus-frame requires data" }

    val direction = parseModbusDirection(
        value = findAttribute(attributes, "direction", "dir", "kind"),
        bytes = bytes
    )
    val accentColor = parseWidgetColor(findAttribute(attributes, "accent", "color"), defaultModbusAccent(direction))
    val manualFields = splitWidgetList(findAttribute(attributes, "fields"), ';')
        .map(::parseModbusFieldRow)
        .filter { field -> field.name.isNotBlank() || field.range.isNotBlank() }
    val preset = findAttribute(attributes, "preset", "auto", "decode")?.trim()?.lowercase()
    val autoFieldsEnabled = when (preset) {
        "rtu", "auto", "true", "on" -> true
        "off", "false", "none" -> false
        else -> manualFields.isEmpty()
    }
    val autoFields = if (autoFieldsEnabled) {
        buildModbusRtuFields(bytes, direction)
    } else {
        emptyList()
    }
    val fields = (autoFields + manualFields).distinctBy { listOf(it.range, it.name, it.value, it.description) }

    return ConsoleWidgetSpec.ModbusFrame(
        title = findAttribute(attributes, "title", "label") ?: buildModbusRtuTitle(bytes, direction),
        direction = direction,
        bytes = bytes,
        fields = fields,
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        accentColor = accentColor,
        byteColor = parseWidgetColor(findAttribute(attributes, "byteColor"), Color(0xFFE3EEF5)),
        fieldNameColor = parseWidgetColor(findAttribute(attributes, "fieldNameColor"), Color.White),
        fieldMetaColor = parseWidgetColor(findAttribute(attributes, "fieldMetaColor", "metaColor"), Color(0xFF8FA1AD)),
        fieldDescriptionColor = parseWidgetColor(findAttribute(attributes, "fieldDescriptionColor", "descColor"), Color(0xFFB5C0C8))
    )
}

/**
 * Parses `type=can-frame`.
 *
 * Example network command:
 * `ui type=can-frame title="Motor CAN" direction=rx id=0x18FF50E5 ext=true data="11 22 33 44 55 66 77 88" channel=can0`
 */
fun parseCanFrameWidget(attributes: Map<String, String>): ConsoleWidgetSpec.CanFrame {
    val direction = parseFrameDirection(findAttribute(attributes, "direction", "dir"), FrameDirection.Rx)
    val extended = parseWidgetBoolean(findAttribute(attributes, "ext", "extended"), false)
    val bytes = parseWidgetHexByteList(findAttribute(attributes, "data", "bytes").orEmpty())
    val dlc = findAttribute(attributes, "dlc")
        ?.toIntOrNull()
        ?.coerceIn(0, 64)
        ?: bytes.size.coerceIn(0, 64)
    val frameId = parseWidgetFlexibleInt(requiredAttribute(attributes, "id", "canId"), 0)
    val fields = splitWidgetList(findAttribute(attributes, "fields"), ';')
        .map(::parsePacketFieldRow)
        .filter { field -> field.name.isNotBlank() || field.range.isNotBlank() }

    return ConsoleWidgetSpec.CanFrame(
        title = findAttribute(attributes, "title", "label") ?: buildString {
            append(if (extended) "CAN EXT " else "CAN ")
            append(formatCanId(frameId, extended))
        },
        direction = direction,
        frameId = frameId,
        bytes = bytes,
        dlc = dlc,
        channel = findAttribute(attributes, "channel", "bus", "port"),
        extended = extended,
        remote = parseWidgetBoolean(findAttribute(attributes, "rtr", "remote"), false),
        fd = parseWidgetBoolean(findAttribute(attributes, "fd"), false),
        bitrateSwitch = parseWidgetBoolean(findAttribute(attributes, "brs"), false),
        fields = fields,
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        accentColor = parseWidgetColor(findAttribute(attributes, "accent", "color"), defaultFrameAccent(direction)),
        byteColor = parseWidgetColor(findAttribute(attributes, "byteColor"), Color(0xFFE3EEF5)),
        metaColor = parseWidgetColor(findAttribute(attributes, "metaColor"), Color(0xFF8FA1AD)),
        fieldNameColor = parseWidgetColor(findAttribute(attributes, "fieldNameColor"), Color.White),
        fieldMetaColor = parseWidgetColor(findAttribute(attributes, "fieldMetaColor"), Color(0xFF8FA1AD)),
        fieldDescriptionColor = parseWidgetColor(findAttribute(attributes, "fieldDescriptionColor", "descColor"), Color(0xFFB5C0C8))
    )
}

/**
 * Parses `type=uart-frame` and `type=packet-frame`.
 *
 * Example network command:
 * `ui type=uart-frame title="UART RX" direction=rx channel=UART1 baud=115200 data="AA 55 10 02 01 02 34" fields="0-1|Sync|AA55|Preamble;2|Cmd|10|Command"`
 */
fun parsePacketFrameWidget(
    attributes: Map<String, String>,
    defaultProtocol: String? = null
): ConsoleWidgetSpec.PacketFrame {
    val direction = parseFrameDirection(findAttribute(attributes, "direction", "dir"), FrameDirection.Rx)
    val bytes = parseWidgetHexByteList(requiredAttribute(attributes, "data", "bytes", "frame"))
    require(bytes.isNotEmpty()) { "packet-frame requires data" }

    val fields = splitWidgetList(findAttribute(attributes, "fields"), ';')
        .map(::parsePacketFieldRow)
        .filter { field -> field.name.isNotBlank() || field.range.isNotBlank() }

    return ConsoleWidgetSpec.PacketFrame(
        title = findAttribute(attributes, "title", "label"),
        protocol = findAttribute(attributes, "protocol", "proto") ?: defaultProtocol,
        direction = direction,
        bytes = bytes,
        channel = findAttribute(attributes, "channel", "port", "bus"),
        baud = findAttribute(attributes, "baud", "speed"),
        fields = fields,
        showAscii = parseWidgetBoolean(findAttribute(attributes, "ascii", "text"), true),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        accentColor = parseWidgetColor(findAttribute(attributes, "accent", "color"), defaultFrameAccent(direction)),
        byteColor = parseWidgetColor(findAttribute(attributes, "byteColor"), Color(0xFFE3EEF5)),
        metaColor = parseWidgetColor(findAttribute(attributes, "metaColor"), Color(0xFF8FA1AD)),
        fieldNameColor = parseWidgetColor(findAttribute(attributes, "fieldNameColor"), Color.White),
        fieldMetaColor = parseWidgetColor(findAttribute(attributes, "fieldMetaColor"), Color(0xFF8FA1AD)),
        fieldDescriptionColor = parseWidgetColor(findAttribute(attributes, "fieldDescriptionColor", "descColor"), Color(0xFFB5C0C8))
    )
}

private fun parseRegisterTableRow(token: String): RegisterTableRow {
    val parts = token.split('|')
        .map { it.trim() }

    return RegisterTableRow(
        address = parts.getOrNull(0).orEmpty(),
        value = parts.getOrNull(1).orEmpty(),
        description = parts.getOrNull(2)?.takeIf { it.isNotBlank() }
    )
}

private fun parseModbusFieldRow(token: String): ModbusFieldRow {
    val parts = token.split('|')
        .map { it.trim() }

    return ModbusFieldRow(
        range = parts.getOrNull(0).orEmpty(),
        name = parts.getOrNull(1).orEmpty(),
        value = parts.getOrNull(2)?.takeIf { it.isNotBlank() },
        description = parts.getOrNull(3)?.takeIf { it.isNotBlank() }
    )
}

private fun parsePacketFieldRow(token: String): PacketFieldRow {
    val parts = token.split('|')
        .map { it.trim() }

    return PacketFieldRow(
        range = parts.getOrNull(0).orEmpty(),
        name = parts.getOrNull(1).orEmpty(),
        value = parts.getOrNull(2)?.takeIf { it.isNotBlank() },
        description = parts.getOrNull(3)?.takeIf { it.isNotBlank() }
    )
}

private fun parseModbusDirection(value: String?, bytes: List<Int>): ModbusDirection {
    if (!value.isNullOrBlank()) {
        return when (value.trim().lowercase()) {
            "request", "req", "tx" -> ModbusDirection.Request
            "response", "resp", "rx" -> ModbusDirection.Response
            "error", "exception", "err" -> ModbusDirection.Error
            else -> ModbusDirection.Request
        }
    }

    val function = bytes.getOrNull(1) ?: return ModbusDirection.Request
    if ((function and 0x80) != 0) return ModbusDirection.Error

    return if (isLikelyModbusResponse(function, bytes)) {
        ModbusDirection.Response
    } else {
        ModbusDirection.Request
    }
}

private fun parseFrameDirection(value: String?, default: FrameDirection): FrameDirection {
    return when (value?.trim()?.lowercase()) {
        "tx", "request", "req", "out" -> FrameDirection.Tx
        "rx", "response", "resp", "in" -> FrameDirection.Rx
        "error", "err", "exception" -> FrameDirection.Error
        else -> default
    }
}

private fun defaultModbusAccent(direction: ModbusDirection): Color {
    return when (direction) {
        ModbusDirection.Request -> Color(0xFF4FC3F7)
        ModbusDirection.Response -> Color(0xFF36C36B)
        ModbusDirection.Error -> Color(0xFFFF7043)
    }
}

private fun defaultFrameAccent(direction: FrameDirection): Color {
    return when (direction) {
        FrameDirection.Tx -> Color(0xFF4FC3F7)
        FrameDirection.Rx -> Color(0xFF36C36B)
        FrameDirection.Error -> Color(0xFFFF7043)
    }
}

private fun buildModbusRtuTitle(bytes: List<Int>, direction: ModbusDirection): String? {
    val function = bytes.getOrNull(1) ?: return null
    val functionName = modbusFunctionName(function and 0x7F) ?: return null
    return when (direction) {
        ModbusDirection.Request -> "$functionName Request"
        ModbusDirection.Response -> "$functionName Response"
        ModbusDirection.Error -> "$functionName Exception"
    }
}

private fun buildModbusRtuFields(bytes: List<Int>, direction: ModbusDirection): List<ModbusFieldRow> {
    if (bytes.size < 4) return emptyList()

    val rawFunction = bytes.getOrNull(1) ?: return emptyList()
    val function = rawFunction and 0x7F
    val rows = mutableListOf<ModbusFieldRow>()

    rows += ModbusFieldRow("0", "Addr", formatByte(bytes[0]), "Slave ID")
    rows += ModbusFieldRow("1", "Func", formatByte(rawFunction), modbusFunctionName(function) ?: "Function")

    if ((rawFunction and 0x80) != 0 || direction == ModbusDirection.Error) {
        bytes.getOrNull(2)?.let { exceptionCode ->
            rows += ModbusFieldRow("2", "Exception", formatByte(exceptionCode), modbusExceptionName(exceptionCode))
        }
        if (bytes.size >= 5) {
            rows += ModbusFieldRow("${bytes.size - 2}-${bytes.size - 1}", "CRC", formatBytes(bytes.takeLast(2)), "CRC16")
        }
        return rows
    }

    when (direction) {
        ModbusDirection.Request -> rows += buildModbusRequestFields(function, bytes)
        ModbusDirection.Response -> rows += buildModbusResponseFields(function, bytes)
        ModbusDirection.Error -> Unit
    }

    if (bytes.size >= 4) {
        rows += ModbusFieldRow("${bytes.size - 2}-${bytes.size - 1}", "CRC", formatBytes(bytes.takeLast(2)), "CRC16")
    }

    return rows
}

private fun buildModbusRequestFields(function: Int, bytes: List<Int>): List<ModbusFieldRow> {
    if (bytes.size < 8) return emptyList()

    val rows = mutableListOf<ModbusFieldRow>()
    when (function) {
        0x01, 0x02, 0x03, 0x04 -> {
            rows += ModbusFieldRow("2-3", "Start", formatWord(bytes, 2), "Address")
            rows += ModbusFieldRow("4-5", "Count", formatWord(bytes, 4), "Quantity")
        }

        0x05 -> {
            rows += ModbusFieldRow("2-3", "Coil", formatWord(bytes, 2), "Coil address")
            rows += ModbusFieldRow("4-5", "Value", formatWord(bytes, 4), "Write state")
        }

        0x06 -> {
            rows += ModbusFieldRow("2-3", "Register", formatWord(bytes, 2), "Register address")
            rows += ModbusFieldRow("4-5", "Value", formatWord(bytes, 4), "Write value")
        }

        0x0F, 0x10 -> {
            rows += ModbusFieldRow("2-3", "Start", formatWord(bytes, 2), "Address")
            rows += ModbusFieldRow("4-5", "Count", formatWord(bytes, 4), "Quantity")
            bytes.getOrNull(6)?.let { byteCount ->
                rows += ModbusFieldRow("6", "ByteCount", formatByte(byteCount), "Payload size")
                val dataEnd = (7 + byteCount).coerceAtMost(bytes.size - 2)
                if (dataEnd > 7) {
                    rows += ModbusFieldRow("7-${dataEnd - 1}", "Data", formatBytes(bytes.subList(7, dataEnd)), "Payload")
                }
            }
        }
    }
    return rows
}

private fun buildModbusResponseFields(function: Int, bytes: List<Int>): List<ModbusFieldRow> {
    val rows = mutableListOf<ModbusFieldRow>()
    when (function) {
        0x01, 0x02, 0x03, 0x04 -> {
            bytes.getOrNull(2)?.let { byteCount ->
                rows += ModbusFieldRow("2", "ByteCount", formatByte(byteCount), "Payload size")
                val dataEnd = (3 + byteCount).coerceAtMost(bytes.size - 2)
                if (dataEnd > 3) {
                    rows += ModbusFieldRow("3-${dataEnd - 1}", "Data", formatBytes(bytes.subList(3, dataEnd)), "Payload")
                }
            }
        }

        0x05 -> {
            if (bytes.size >= 8) {
                rows += ModbusFieldRow("2-3", "Coil", formatWord(bytes, 2), "Coil address")
                rows += ModbusFieldRow("4-5", "Value", formatWord(bytes, 4), "Written state")
            }
        }

        0x06 -> {
            if (bytes.size >= 8) {
                rows += ModbusFieldRow("2-3", "Register", formatWord(bytes, 2), "Register address")
                rows += ModbusFieldRow("4-5", "Value", formatWord(bytes, 4), "Written value")
            }
        }

        0x0F, 0x10 -> {
            if (bytes.size >= 8) {
                rows += ModbusFieldRow("2-3", "Start", formatWord(bytes, 2), "Address")
                rows += ModbusFieldRow("4-5", "Count", formatWord(bytes, 4), "Quantity written")
            }
        }
    }
    return rows
}

private fun isLikelyModbusResponse(function: Int, bytes: List<Int>): Boolean {
    return when (function) {
        0x01, 0x02, 0x03, 0x04 -> bytes.size >= 5 && bytes.size != 8
        0x05, 0x06, 0x0F, 0x10 -> false
        else -> false
    }
}

private fun modbusFunctionName(function: Int): String? {
    return when (function) {
        0x01 -> "Read Coils"
        0x02 -> "Read Discrete Inputs"
        0x03 -> "Read Holding Registers"
        0x04 -> "Read Input Registers"
        0x05 -> "Write Single Coil"
        0x06 -> "Write Single Register"
        0x0F -> "Write Multiple Coils"
        0x10 -> "Write Multiple Registers"
        else -> null
    }
}

private fun modbusExceptionName(code: Int): String {
    return when (code) {
        0x01 -> "Illegal Function"
        0x02 -> "Illegal Data Address"
        0x03 -> "Illegal Data Value"
        0x04 -> "Slave Device Failure"
        0x05 -> "Acknowledge"
        0x06 -> "Slave Device Busy"
        0x08 -> "Memory Parity Error"
        0x0A -> "Gateway Path Unavailable"
        0x0B -> "Gateway Target Failed"
        else -> "Exception"
    }
}

private fun formatCanId(frameId: Int, extended: Boolean): String {
    val width = if (extended) 8 else 3
    return "0x${frameId.toUInt().toString(16).uppercase().padStart(width, '0')}"
}

private fun formatByte(byte: Int): String {
    return byte.toString(16).uppercase().padStart(2, '0')
}

private fun formatWord(bytes: List<Int>, startIndex: Int): String {
    if (startIndex + 1 >= bytes.size) return ""
    return formatBytes(bytes.subList(startIndex, startIndex + 2))
}

private fun formatBytes(bytes: List<Int>): String {
    return bytes.joinToString("") { byte -> formatByte(byte) }
}
