package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.terminalm3.console.formatWidgetNumber

/**
 * Compose renderer for [ConsoleWidgetSpec.BarGroup].
 *
 * Request that creates this preview:
 * `ui type=bar-group title="Motors" labels="M1|M2|M3" values="20|45|80" max=100 colors="#36C36B|#4FC3F7|#FFB300"`
 */
@Composable
fun BarGroupConsoleWidget(spec: ConsoleWidgetSpec.BarGroup) {
    val resolvedMax = (spec.max ?: spec.values.maxOrNull() ?: 0f).coerceAtLeast(0.0001f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        spec.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                color = spec.titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            spec.values.forEachIndexed { index, rawValue ->
                val value = rawValue.coerceAtLeast(0f)
                val fraction = (value / resolvedMax).coerceIn(0f, 1f)
                val label = spec.labels.getOrElse(index) { "" }
                val barColor = spec.colors.getOrNull(index) ?: spec.barColor

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatWidgetNumber(value),
                        color = spec.valueColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .height(88.dp)
                            .width(24.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(spec.backgroundColor.copy(alpha = 0.34f)),
                            contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(if (fraction == 0f) 0f else fraction.coerceAtLeast(0.05f))
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(999.dp))
                                .background(barColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = label,
                        color = spec.labelColor,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(name = "Bar Group", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewBarGroupConsoleWidget() {
    WidgetPreviewSurface {
        BarGroupConsoleWidget(previewBarGroupSpec())
    }
}
