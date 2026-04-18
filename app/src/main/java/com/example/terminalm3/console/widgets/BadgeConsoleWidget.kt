package com.example.terminalm3.console.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.console.ConsoleWidgetSpec

/**
 * Compose renderer for [ConsoleWidgetSpec.Badge].
 *
 * Request that creates this preview:
 * `ui type=badge text="READY" st=ok`
 */
@Composable
fun BadgeConsoleWidget(spec: ConsoleWidgetSpec.Badge) {
    Text(
        text = spec.text,
        color = spec.textColor,
        fontSize = spec.fontSizeSp.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(spec.backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Preview(name = "Badge", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeConsoleWidget() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeSpec())
    }
}

@Preview(
    name = "Badge Styles Gallery",
    showBackground = true,
    backgroundColor = CONSOLE_WIDGET_PREVIEW_BG,
    widthDp = 360
)
@Composable
private fun PreviewBadgeStylesGallery() {
    WidgetPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BadgeConsoleWidget(previewBadgeOkSpec())
            BadgeConsoleWidget(previewBadgeInfoSpec())
            BadgeConsoleWidget(previewBadgeWarnSpec())
            BadgeConsoleWidget(previewBadgeErrorSpec())
            BadgeConsoleWidget(previewBadgeCriticalSpec())
            BadgeConsoleWidget(previewBadgeNeutralSpec())
            BadgeConsoleWidget(previewBadgeDarkSpec())
        }
    }
}

@Preview(name = "Badge • ok", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeOkStyle() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeOkSpec())
    }
}

@Preview(name = "Badge • info", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeInfoStyle() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeInfoSpec())
    }
}

@Preview(name = "Badge • warn", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeWarnStyle() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeWarnSpec())
    }
}

@Preview(name = "Badge • error", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeErrorStyle() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeErrorSpec())
    }
}

@Preview(name = "Badge • critical", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeCriticalStyle() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeCriticalSpec())
    }
}

@Preview(name = "Badge • neutral", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeNeutralStyle() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeNeutralSpec())
    }
}

@Preview(name = "Badge • dark", showBackground = true, backgroundColor = CONSOLE_WIDGET_PREVIEW_BG, widthDp = 360)
@Composable
private fun PreviewBadgeDarkStyle() {
    WidgetPreviewSurface {
        BadgeConsoleWidget(previewBadgeDarkSpec())
    }
}

private fun previewBadgeOkSpec() = previewBadgeStyleSpec(
    text = "READY",
    backgroundColor = Color(0xFF1F7A1F)
)

private fun previewBadgeInfoSpec() = previewBadgeStyleSpec(
    text = "INFO",
    backgroundColor = Color(0xFF174A7A)
)

private fun previewBadgeWarnSpec() = previewBadgeStyleSpec(
    text = "WAIT",
    backgroundColor = Color(0xFF7A5B12)
)

private fun previewBadgeErrorSpec() = previewBadgeStyleSpec(
    text = "FAIL",
    backgroundColor = Color(0xFF7A1F1F)
)

private fun previewBadgeCriticalSpec() = previewBadgeStyleSpec(
    text = "ALARM",
    backgroundColor = Color(0xFF5A1010),
    textColor = Color(0xFFFFE7E7)
)

private fun previewBadgeNeutralSpec() = previewBadgeStyleSpec(
    text = "IDLE",
    backgroundColor = Color(0xFF33414D)
)

private fun previewBadgeDarkSpec() = previewBadgeStyleSpec(
    text = "MUTED",
    backgroundColor = Color(0xFF1D252C),
    textColor = Color(0xFFE3EEF5)
)

private fun previewBadgeStyleSpec(
    text: String,
    backgroundColor: Color,
    textColor: Color = Color.White
) = ConsoleWidgetSpec.Badge(
    text = text,
    textColor = textColor,
    backgroundColor = backgroundColor,
    fontSizeSp = 14
)
