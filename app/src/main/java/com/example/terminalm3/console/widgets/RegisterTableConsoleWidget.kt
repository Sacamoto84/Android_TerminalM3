package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec
import com.example.terminalm3.console.RegisterTableRow

/**
 * Compose renderer for [ConsoleWidgetSpec.RegisterTable].
 *
 * Request that creates this preview:
 * `ui type=register-table title="Holding Registers" rows="0000|0x1234|Status;0001|0x00A5|Flags;0002|0x03E8|Speed"`
 */
@Composable
fun RegisterTableConsoleWidget(spec: ConsoleWidgetSpec.RegisterTable) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        spec.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                color = spec.titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        RegisterTableHeader(spec)

        spec.rows.forEachIndexed { index, row ->
            RegisterTableRowView(
                row = row,
                spec = spec,
                rowBackground = if (index % 2 == 0) spec.backgroundColor.copy(alpha = 0.22f) else spec.backgroundColor.copy(alpha = 0.10f)
            )
        }
    }
}

@Composable
private fun RegisterTableHeader(spec: ConsoleWidgetSpec.RegisterTable) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(spec.headerBackgroundColor)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ADDR",
            color = spec.headerTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = "VALUE",
            color = spec.headerTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1.1f)
        )
        Text(
            text = "DESC",
            color = spec.headerTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.8f)
        )
    }
}

@Composable
private fun RegisterTableRowView(
    row: RegisterTableRow,
    spec: ConsoleWidgetSpec.RegisterTable,
    rowBackground: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(rowBackground)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = row.address,
            color = spec.addressColor,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = row.value,
            color = spec.valueColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1.1f)
        )
        Text(
            text = row.description.orEmpty(),
            color = spec.descriptionColor,
            fontSize = 12.sp,
            modifier = Modifier.weight(1.8f)
        )
    }
}

@Preview(name = "Register Table", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewRegisterTableConsoleWidget() {
    WidgetPreviewSurface {
        RegisterTableConsoleWidget(previewRegisterTableSpec())
    }
}
