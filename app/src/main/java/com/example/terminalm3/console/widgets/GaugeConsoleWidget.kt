package com.example.terminalm3.console.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.Gauge].
 *
 * Request that creates this preview:
 * `ui type=gauge label="CPU" value=72 max=100 unit="%" color=#36C36B`
 */
@Composable
fun GaugeConsoleWidget(spec: ConsoleWidgetSpec.Gauge) {
    val fraction = (spec.value / spec.max).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        spec.label?.takeIf { it.isNotBlank() }?.let { label ->
            Text(
                text = label,
                color = spec.labelColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val arcPadding = strokeWidth / 2f
                val arcSize = size.minDimension - strokeWidth

                drawArc(
                    color = spec.trackColor,
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(arcPadding, arcPadding),
                    size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                drawArc(
                    color = spec.color,
                    startAngle = 150f,
                    sweepAngle = 240f * fraction,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(arcPadding, arcPadding),
                    size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = spec.text ?: buildString {
                        append(spec.value.toInt())
                        spec.unit?.let { append(it) }
                    },
                    color = spec.valueColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${(fraction * 100f).toInt()}%",
                    color = spec.valueColor.copy(alpha = 0.72f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(name = "Gauge", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewGaugeConsoleWidget() {
    WidgetPreviewSurface {
        GaugeConsoleWidget(previewGaugeSpec())
    }
}
