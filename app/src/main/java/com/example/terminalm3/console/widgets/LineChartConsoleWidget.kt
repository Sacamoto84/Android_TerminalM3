package com.example.terminalm3.console.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec
import com.example.terminalm3.console.formatWidgetNumber

/**
 * Compose renderer for [ConsoleWidgetSpec.LineChart].
 *
 * Request that creates this preview:
 * `ui type=line-chart title="Voltage" values="24.1,24.2,24.0,24.3,24.4" labels="T1|T2|T3|T4|T5" min=23 max=25 color=#4FC3F7`
 */
@Composable
fun LineChartConsoleWidget(spec: ConsoleWidgetSpec.LineChart) {
    val resolvedMin = spec.min ?: spec.values.minOrNull() ?: 0f
    val rawMax = spec.max ?: spec.values.maxOrNull() ?: 1f
    val resolvedMax = if (rawMax > resolvedMin) rawMax else resolvedMin + 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(spec.backgroundColor)
            .border(1.dp, spec.borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            spec.title?.takeIf { it.isNotBlank() }?.let { title ->
                Text(
                    text = title,
                    color = spec.titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${formatWidgetNumber(resolvedMin)} - ${formatWidgetNumber(resolvedMax)}",
                color = spec.labelColor,
                fontSize = 12.sp
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            if (spec.values.isEmpty()) return@Canvas

            val range = (resolvedMax - resolvedMin).coerceAtLeast(0.0001f)
            val graphWidth = size.width
            val graphHeight = size.height

            repeat(4) { index ->
                val y = graphHeight * index / 3f
                drawLine(
                    color = spec.axisColor.copy(alpha = 0.55f),
                    start = Offset(0f, y),
                    end = Offset(graphWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val points = spec.values.mapIndexed { index, value ->
                val x = if (spec.values.size == 1) {
                    graphWidth / 2f
                } else {
                    graphWidth * index / spec.values.lastIndex.toFloat()
                }
                val fraction = ((value - resolvedMin) / range).coerceIn(0f, 1f)
                val y = graphHeight - (fraction * graphHeight)
                Offset(x, y)
            }

            if (points.size == 1) {
                drawCircle(
                    color = spec.color,
                    radius = 4.dp.toPx(),
                    center = points.first()
                )
                return@Canvas
            }

            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { point -> lineTo(point.x, point.y) }
            }
            val fillPath = Path().apply {
                moveTo(points.first().x, graphHeight)
                lineTo(points.first().x, points.first().y)
                points.drop(1).forEach { point -> lineTo(point.x, point.y) }
                lineTo(points.last().x, graphHeight)
                close()
            }

            drawPath(
                path = fillPath,
                color = spec.fillColor
            )
            drawPath(
                path = linePath,
                color = spec.color,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            if (spec.showDots) {
                points.forEach { point ->
                    drawCircle(
                        color = spec.color,
                        radius = 3.5.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        if (spec.labels.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                spec.labels.forEach { label ->
                    Text(
                        text = label,
                        color = spec.labelColor,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(name = "Line Chart", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 420)
@Composable
private fun PreviewLineChartConsoleWidget() {
    WidgetPreviewSurface {
        LineChartConsoleWidget(previewLineChartSpec())
    }
}
