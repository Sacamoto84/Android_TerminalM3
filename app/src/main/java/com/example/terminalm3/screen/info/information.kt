package com.example.terminalm3.screen.info

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terminalm3.R
import com.example.terminalm3.colorIn256
import com.example.terminalm3.console
import com.example.terminalm3.listSortedColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenInfo(navController: NavController) {

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF090909))
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .weight(1f)
        )
        {


            FlowRow(maxItemsInEachRow = 8) {

                for (i in 0..15)
                {
                    val textcolor = when (i) {
                        in 0..4 -> Color(0xFFBBBBBB)
                        else -> Color.Black
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(30.dp)
                        .background(colorIn256(i))
                        .border(0.5.dp, Color.Black),
                        contentAlignment = Alignment.Center)
                    { Text(text = i.toString(), fontSize = 14.sp, fontFamily = FontFamily.Monospace, color= textcolor)}
                }
            }

            FlowRow(maxItemsInEachRow = 12) {

                listSortedColor.forEach{ value ->

                    val textcolor = when (value) {
                        in 0..4, in 16..27, in 52..57 , in 232..243 -> Color(0xFFBBBBBB)
                        else -> Color.Black
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(30.dp)
                        .background(colorIn256(value))
                        .border(0.5.dp, Color.Black),
                        contentAlignment = Alignment.Center)
                    { Text(text = value.toString(), fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.quicksand)), fontWeight= FontWeight.ExtraBold, color= textcolor)}
                }

            }

            Spacer(modifier = Modifier.height(5.dp))

            var x = buildAnnotatedString {

                withStyle(style = SpanStyle(color = Color.Gray)) {
                    append("""\x1B or \033 or \u001b""")
                }
                withStyle(style = SpanStyle(color = Color.White)) {
                    append("""[""")
                }
                withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                    append("01 - Bold\n")
                }

                withStyle(style = SpanStyle(color = Color.White,fontStyle = FontStyle.Italic)) {
                    append("03 - Italic\n")
                }

                withStyle(style = SpanStyle(color = Color.White,textDecoration = TextDecoration.Underline)) {
                    append("04 - Underline\n")
                }

                withStyle(style = SpanStyle(color = Color.Black, background = Color.White,textDecoration = TextDecoration.Underline)) {
                    append("07 - Revers\n")
                }

                withStyle(style = SpanStyle(color = if (console.update.collectAsState().value) Color.White else Color.Transparent)) {
                    append("08 - Flash +\n")
                }

                withStyle(style = SpanStyle(color = Color.White)) {
                    append("38;05;xxx Цвет текста\n")
                }

                withStyle(style = SpanStyle(color = Color.White)) {
                    append("48;05;xxx Цвет Фона\n")
                }

                withStyle(style = SpanStyle(color = Color.White)) {
                    append("\\x1B[0m Сброс настроек цвета\n")
                    append("\\x1B[1m + Очистка экрана\n")
                    append("\\x1B[3m + Звуковой сигнал (Не готово)\n")
                    append("\n\\x1B[01;03;38;05;147;48;05;21m Текст \\x1B[0m\n")
                    append("\nUDP Порт 8888")
                }

            }

            Text(x)

        }

        BottomNavigationInfo(navController)

    }
}



