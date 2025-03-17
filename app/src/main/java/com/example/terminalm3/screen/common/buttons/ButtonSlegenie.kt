package com.example.terminalm3.screen.common.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    val slegenie = console.tracking

    // Кнопка включения слежения
    Button(modifier = Modifier
        //.height(32.dp)
        .fillMaxWidth(0.6f)
        .padding(
            //start = 8.dp,
            //top = 8.dp,
            //bottom = 8.dp
        )

        .then(modifier),
        contentPadding = PaddingValues(0.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (slegenie) Color(0xFF8AAF4A) else Color.DarkGray
        ),
        onClick = {
            console.tracking = !console.tracking
        }) {
        console.recompose()
        Text(
            text = "${console.lastCount}",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.jetbrains)),
            fontSize = 18.sp
        )
    }
}


