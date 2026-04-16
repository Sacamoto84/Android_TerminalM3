package com.example.terminalm3.screen.common.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.terminalm3.R
import com.example.terminalm3.console

@Preview(apiLevel = 31)
@Composable
private fun Preview(){
    ButtonSlegenie()
}

@Composable
fun ButtonSlegenie(modifier: Modifier = Modifier) {

    //По кнопке включаем слежение
    val count = console.lastCount

    // Кнопка включения слежения
    Button(modifier = Modifier
        .padding(horizontal = 4.dp)
        .then(modifier),
        contentPadding = PaddingValues(0.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF505050)
        ),
        onClick = {})
    {
        Text(
            text = "$count",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.jetbrains)),
            fontSize = 18.sp
        )
    }
}
