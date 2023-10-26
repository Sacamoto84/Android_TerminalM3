package com.example.terminalm3.screen.lazy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.terminalm3.R
import com.example.terminalm3.warning

@Composable
fun Warning()
{
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    )
    {
        val image: Painter = painterResource(id = R.drawable.error)

        if (warning.collectAsState().value) {
            Image(
                painter = image,
                contentDescription = "",
                Modifier
                    .size(48.dp)
                    .padding(end = 10.dp)
            )
        }
    }
}