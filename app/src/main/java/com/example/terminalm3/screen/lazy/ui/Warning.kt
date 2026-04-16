package com.example.terminalm3.screen.lazy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.terminalm3.Global
import com.example.terminalm3.R
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer

@Composable
fun Warning(modifier: Modifier = Modifier) {
    val isWarningVisible = Global.warning.collectAsState().value
    val liveDataWarningState = remember { mutableStateOf(Global.telnetWarning.value == true) }

    DisposableEffect(Unit) {
        val observer = Observer<Boolean> { liveDataWarningState.value = it == true }
        Global.telnetWarning.observeForever(observer)
        onDispose {
            Global.telnetWarning.removeObserver(observer)
        }
    }

    if (!isWarningVisible && !liveDataWarningState.value) return

    val image: Painter = painterResource(id = R.drawable.error)
    Image(
        painter = image,
        contentDescription = "Warning",
        modifier = modifier.size(48.dp)
    )
}
