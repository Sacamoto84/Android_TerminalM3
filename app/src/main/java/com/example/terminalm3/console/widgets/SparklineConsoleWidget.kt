package com.example.terminalm3.console.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.Sparkline].
 *
 * Request that creates this preview:
 * `ui type=sparkline label="Temp" values="21,22,22,23,24,23,25" min=18 max=28 color=#36C36B display="25C" points=on`
 */
@Composable
fun SparklineConsoleWidget(spec: ConsoleWidgetSpec.Sparkline) {
    val resolvedMin = spec.min ?: spec.values.minOrNull() ?: 0f
    val rawMax = spec.max ?: spec.values.maxOrNull() ?: 1f
    val resolvedMax = if (rawMax > resolvedMin) rawMax else resolvedMin + 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        if (!spec.label.isNullOrBlank() || !spec.text.isNullOrBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                spec.label?.takeIf { it.isNotBlank() }?.let { label ->
                    Text(
                        text = label,
                        color = spec.labelColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                } ?: Spacer(modifier = Modifier.weight(1f))

                spec.text?.takeIf { it.isNotBlank() }?.let { valueText ->
                    Text(
                        text = valueText,
                        color = spec.valueColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
        ) {
            if (spec.values.isEmpty()) return@Canvas

            val range = (resolvedMax - resolvedMin).coerceAtLeast(0.0001f)
            val points = spec.values.mapIndexed { index, value ->
                val x = if (spec.values.size == 1) {
                    size.width / 2f
                } else {
                    size.width * index / (spec.values.lastIndex.toFloat())
                }
                val fraction = ((value - resolvedMin) / range).coerceIn(0f, 1f)
                val y = size.height - (fraction * size.height)
                Offset(x, y)
            }

            if (points.size == 1) {
                drawCircle(
                    color = spec.lineColor,
                    radius = 5.dp.toPx(),
                    center = points.first()
                )
                return@Canvas
            }

            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { point -> lineTo(point.x, point.y) }
            }

            val fillPath = Path().apply {
                moveTo(points.first().x, size.height)
                lineTo(points.first().x, points.first().y)
                points.drop(1).forEach { point -> lineTo(point.x, point.y) }
                lineTo(points.last().x, size.height)
                close()
            }

            drawPath(
                path = fillPath,
                color = spec.fillColor
            )

            drawPath(
                path = linePath,
                color = spec.lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            if (spec.showDots) {
                points.forEach { point ->
                    drawCircle(
                        color = spec.lineColor,
                        radius = 3.5.dp.toPx(),
                        center = point
                    )
                }
            }
        }
    }
}

@Preview(name = "Sparkline", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewSparklineConsoleWidget() {
    WidgetPreviewSurface {
        SparklineConsoleWidget(previewSparklineSpec())
    }
}
