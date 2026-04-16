package com.example.terminalm3.screen.common.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.terminalm3.R
import com.example.terminalm3.console

@Preview(apiLevel = 31)
@Composable
private fun Preview(){
    ButtonScrollEnd()
}

@Composable
fun ButtonScrollEnd(onClick: () -> Unit = {}) {
    val isTracking = console.tracking

    IconButton(
        modifier = Modifier.size(40.dp)
            .dropShadow(
            shape = CircleShape,
            shadow = Shadow(
                radius = 1.dp,
                color = Color.Black.copy(alpha = 0.02f),
                offset = DpOffset(0.dp, 0.dp)
            )
        ),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isTracking) Color(0xFF8AAF4A) else Color(0xFF606060)
        ),
        onClick = {
            console.requestScrollToEnd()
            onClick()
        }
    )
    {
        Icon(
            modifier = Modifier.rotate(-90f),
            painter = painterResource(R.drawable.back),
            tint = if (isTracking) Color.White else Color.LightGray,
            contentDescription = "Scroll to end"
        )
    }
}
