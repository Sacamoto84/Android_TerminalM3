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
    val direction = parseModbusDirection(findAttribute(attributes, "direction", "dir", "kind"))
    val accentColor = parseWidgetColor(findAttribute(attributes, "accent", "color"), defaultModbusAccent(direction))
    val bytes = parseWidgetHexByteList(requiredAttribute(attributes, "data", "bytes", "frame"))
    require(bytes.isNotEmpty()) { "modbus-frame requires data" }

    val fields = splitWidgetList(findAttribute(attributes, "fields"), ';')
        .map(::parseModbusFieldRow)
        .filter { field -> field.name.isNotBlank() || field.range.isNotBlank() }

    return ConsoleWidgetSpec.ModbusFrame(
        title = findAttribute(attributes, "title", "label"),
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

private fun parseModbusDirection(value: String?): ModbusDirection {
    return when (value?.trim()?.lowercase()) {
        "request", "req", "tx" -> ModbusDirection.Request
        "response", "resp", "rx" -> ModbusDirection.Response
        "error", "exception", "err" -> ModbusDirection.Error
        else -> ModbusDirection.Request
    }
}

private fun defaultModbusAccent(direction: ModbusDirection): Color {
    return when (direction) {
        ModbusDirection.Request -> Color(0xFF4FC3F7)
        ModbusDirection.Response -> Color(0xFF36C36B)
        ModbusDirection.Error -> Color(0xFFFF7043)
    }
}
