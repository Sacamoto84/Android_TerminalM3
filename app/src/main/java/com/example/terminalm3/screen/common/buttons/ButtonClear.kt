package com.example.terminalm3.screen.common.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.terminalm3.R
import com.example.terminalm3.console

@Preview(apiLevel = 31)
@Composable
private fun Preview(){
    ButtonClear()
}

@Composable
fun ButtonClear(onClick: () -> Unit = {}) {
    IconButton(
        modifier = Modifier.size(34.dp),
        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF505050)),
        onClick = {
            console.clear()
        }
    )
    {
        Icon(
            painter = painterResource(R.drawable.eraser),
            tint = Color.LightGray,
            contentDescription = null
        )
    }
}