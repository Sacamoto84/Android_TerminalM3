package com.example.terminalm3.console.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.AlarmSeverity

internal const val CONSOLE_WIDGET_PREVIEW_BG = 0xFF090909

@Composable
internal fun WidgetPreviewSurface(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(CONSOLE_WIDGET_PREVIEW_BG))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
internal fun WidgetOptionalIcon(iconName: String?, contentDescription: String, sizeDp: Int) {
    iconName?.takeIf { it.isNotBlank() }?.let { safeName ->
        val drawableId = rememberWidgetDrawableId(safeName)
        if (drawableId != 0) {
            Image(
                painter = painterResource(drawableId),
                contentDescription = contentDescription,
                modifier = Modifier.size(sizeDp.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
internal fun MissingDrawableWidget(drawableName: String) {
    Text(
        text = "Drawable not found: $drawableName",
        color = Color(0xFFFF8A80),
        fontSize = 13.sp
    )
}

@Composable
internal fun rememberWidgetDrawableId(drawableName: String): Int {
    val context = LocalContext.current
    return remember(drawableName) {
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)
    }
}

internal fun List<String>.normalizeWidgetRow(columnCount: Int): List<String> {
    return if (size >= columnCount) {
        take(columnCount)
    } else {
        this + List(columnCount - size) { "" }
    }
}

internal fun severityLabel(severity: AlarmSeverity): String {
    return when (severity) {
        AlarmSeverity.Info -> "INFO"
        AlarmSeverity.Warn -> "WARN"
        AlarmSeverity.Error -> "ERROR"
        AlarmSeverity.Critical -> "CRITICAL"
    }
}
