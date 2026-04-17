package com.example.terminalm3.console

import androidx.compose.ui.graphics.Color

/**
 * Parses `type=bitfield`.
 *
 * Example network command:
 * `ui type=bitfield label="STATUS" value=0xA5 bits=8`
 */
fun parseBitFieldWidget(attributes: Map<String, String>): ConsoleWidgetSpec.BitField {
    val bitCount = parseWidgetBitCount(
        bits = findAttribute(attributes, "bits", "size", "width"),
        kind = findAttribute(attributes, "kind", "format")
    )
    val value = parseWidgetUnsignedLong(requiredAttribute(attributes, "value", "data", "reg"))
    val mask = if (bitCount >= 64) ULong.MAX_VALUE else ((1uL shl bitCount) - 1uL)

    return ConsoleWidgetSpec.BitField(
        label = findAttribute(attributes, "label", "title", "name"),
        value = value and mask,
        bitCount = bitCount,
        setColor = parseWidgetColor(findAttribute(attributes, "setColor", "color", "accent"), Color(0xFF36C36B)),
        clearColor = parseWidgetColor(findAttribute(attributes, "clearColor", "offColor"), Color(0xFF202A31)),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        labelColor = parseWidgetColor(findAttribute(attributes, "labelColor", "fg"), Color.White),
        indexColor = parseWidgetColor(findAttribute(attributes, "indexColor"), Color(0xFF8FA1AD)),
        valueColor = parseWidgetColor(findAttribute(attributes, "valueColor"), Color(0xFFE3EEF5))
    )
}

/**
 * Parses `type=hex-dump`.
 *
 * Example network command:
 * `ui type=hex-dump title="RX Buffer" data="48 65 6C 6C 6F 20 57 6F 72 6C 64" width=8 addr=0x1000 ascii=on`
 */
fun parseHexDumpWidget(attributes: Map<String, String>): ConsoleWidgetSpec.HexDump {
    val bytes = parseWidgetHexByteList(requiredAttribute(attributes, "data", "bytes", "dump"))
    require(bytes.isNotEmpty()) { "hex-dump requires data" }

    return ConsoleWidgetSpec.HexDump(
        title = findAttribute(attributes, "title", "label", "name"),
        bytes = bytes,
        bytesPerRow = findAttribute(attributes, "width", "row", "cols")
            ?.toIntOrNull()
            ?.coerceIn(4, 16)
            ?: 8,
        startAddress = parseWidgetFlexibleInt(findAttribute(attributes, "addr", "address", "offset"), 0),
        showAscii = parseWidgetBoolean(findAttribute(attributes, "ascii", "text"), true),
        backgroundColor = parseWidgetColor(findAttribute(attributes, "bg", "background"), Color(0xFF11171C)),
        borderColor = parseWidgetColor(findAttribute(attributes, "border"), Color(0xFF23303A)),
        titleColor = parseWidgetColor(findAttribute(attributes, "titleColor", "fg"), Color.White),
        addressColor = parseWidgetColor(findAttribute(attributes, "addressColor"), Color(0xFF8FA1AD)),
        byteColor = parseWidgetColor(findAttribute(attributes, "byteColor"), Color(0xFFE3EEF5)),
        asciiColor = parseWidgetColor(findAttribute(attributes, "asciiColor"), Color(0xFFB5C0C8))
    )
}

internal fun parseWidgetBitCount(bits: String?, kind: String?): Int {
    bits?.trim()?.toIntOrNull()?.let { parsed ->
        return parsed.coerceIn(4, 32).let { value ->
            when {
                value <= 8 -> 8
                value <= 16 -> 16
                else -> 32
            }
        }
    }

    return when (kind?.trim()?.lowercase()) {
        "byte", "u8", "int8", "8" -> 8
        "short", "u16", "int16", "16" -> 16
        "word", "dword", "int", "u32", "int32", "32" -> 32
        else -> 8
    }
}

internal fun parseWidgetUnsignedLong(value: String): ULong {
    val normalized = value.trim().lowercase()
    return when {
        normalized.startsWith("0x") -> normalized.removePrefix("0x").toULong(16)
        normalized.startsWith("0b") -> normalized.removePrefix("0b").toULong(2)
        normalized.all { it.isDigit() } -> normalized.toULong()
        normalized.matches(Regex("[0-9a-f]+")) -> normalized.toULong(16)
        else -> error("Unsupported integer format: $value")
    }
}

internal fun parseWidgetFlexibleInt(value: String?, default: Int): Int {
    if (value.isNullOrBlank()) return default
    val normalized = value.trim().lowercase()
    return when {
        normalized.startsWith("0x") -> normalized.removePrefix("0x").toInt(16)
        normalized.startsWith("0b") -> normalized.removePrefix("0b").toInt(2)
        else -> normalized.toIntOrNull() ?: default
    }
}

internal fun parseWidgetHexByteList(raw: String): List<Int> {
    val normalized = raw.trim()
    if (normalized.isBlank()) return emptyList()

    val tokens = when {
        normalized.contains(' ') || normalized.contains(',') || normalized.contains(';') || normalized.contains('|') -> {
            normalized.split(Regex("[\\s,;|]+"))
        }

        normalized.matches(Regex("(?i)[0-9a-f]+")) && normalized.length % 2 == 0 -> {
            normalized.chunked(2)
        }

        else -> listOf(normalized)
    }

    return tokens
        .mapNotNull { token ->
            val clean = token.trim()
                .removePrefix("0x")
                .removePrefix("0X")
                .takeIf { it.isNotEmpty() }
            clean?.toIntOrNull(16)
        }
        .map { it.coerceIn(0, 0xFF) }
}
