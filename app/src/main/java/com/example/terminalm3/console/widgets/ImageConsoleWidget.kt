package com.example.terminalm3.console.widgets

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.Image].
 */
@Composable
fun ImageConsoleWidget(spec: ConsoleWidgetSpec.Image) {
    val drawableId = rememberWidgetDrawableId(spec.drawableName)

    if (drawableId == 0) {
        MissingDrawableWidget(spec.drawableName)
        return
    }

    Image(
        painter = painterResource(drawableId),
        contentDescription = spec.description,
        modifier = Modifier.size(spec.sizeDp.dp)
    )
}

@Preview(name = "Image", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewImageConsoleWidget() {
    WidgetPreviewSurface {
        ImageConsoleWidget(
            ConsoleWidgetSpec.Image(
                drawableName = "info",
                sizeDp = 40,
                description = "Info icon"
            )
        )
    }
}
