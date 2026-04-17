package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec
import com.example.terminalm3.console.PinBankItem

/**
 * Compose renderer for [ConsoleWidgetSpec.PinBank].
 *
 * Request that creates this preview:
 * `ui type=pin-bank title="GPIO" items="D1:on|D2:off|D3:warn|A0:adc|PWM1:pwm"`
 */
@Composable
fun PinBankConsoleWidget(spec: ConsoleWidgetSpec.PinBank) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        spec.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                color = spec.titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        spec.items.chunked(spec.columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    PinBankCell(
                        item = item,
                        spec = spec,
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(spec.columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PinBankCell(
    item: PinBankItem,
    spec: ConsoleWidgetSpec.PinBank,
    modifier: Modifier = Modifier
) {
    val indicatorColor = if (item.isActive) item.color else item.color.copy(alpha = 0.72f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(spec.backgroundColor.copy(alpha = 0.28f))
            .border(1.dp, spec.borderColor.copy(alpha = 0.72f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(indicatorColor)
                    .padding(5.dp)
            )
            Text(
                text = item.pin,
                color = spec.pinColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = item.state,
            color = if (item.isActive) indicatorColor else spec.stateColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(name = "Pin Bank", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewPinBankConsoleWidget() {
    WidgetPreviewSurface {
        PinBankConsoleWidget(previewPinBankSpec())
    }
}
