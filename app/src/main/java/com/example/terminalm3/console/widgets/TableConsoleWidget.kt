package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.Table].
 *
 * Request that creates this preview:
 * `ui type=table headers="Name|State|Temp" rows="M1|READY|24.3;M2|WAIT|22.9;M3|ALARM|91.8"`
 */
@Composable
fun TableConsoleWidget(spec: ConsoleWidgetSpec.Table) {
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
            TableRow(
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

            TableRow(
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

@Composable
private fun TableRow(
    cells: List<String>,
    textColor: androidx.compose.ui.graphics.Color,
    backgroundColor: androidx.compose.ui.graphics.Color,
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

@Preview(name = "Table", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewTableConsoleWidget() {
    WidgetPreviewSurface {
        TableConsoleWidget(previewTableSpec())
    }
}
