package com.example.terminalm3.screen.lazy.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.terminalm3.global
import com.example.terminalm3.ipAddress
import com.siddroid.holi.colors.MaterialColor

@Preview
@Composable
private fun Preview() {
    CardIpAddress()
}

@Composable
fun CardIpAddress() {

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 8.dp, end = 8.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialColor.GREY_900)
    )
    {
        Text(
            text = "IP адрес телефона $ipAddress",
            color = Color.White,
            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
        )
        val str = if (global.ipESP[0] == '/') global.ipESP.removePrefix("/") else global.ipESP
        Text(
            text = "IP адрес esp.local   $str",
            color = Color.White,
            modifier = Modifier.padding(start = 20.dp, bottom = 5.dp)
        )
    }

}