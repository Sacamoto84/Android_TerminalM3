package com.example.terminalm3.screen.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.terminalm3.colorIn256

//MARK: Локальные дефайны
val textSize = 12.sp
val fontWeight = FontWeight.Normal
val boxSize = 32.dp


//// Определяем компонент для отрисовки графики с помощью OpenGl
//@Composable
//fun OpenGlView(vectorAsset: VectorAsset){
//    Box(){
//        OpenGLView(vectorAsset=vectorAsset)
//    }
//}


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

            Text(
                text = """  \x1B \033 \u001b | 38;05;xxx Text | 48;05;xxx Bg""",
                color = Color.White
            )
            Text(
                text = "  01 Bold | 03 Italic | 04 Underline | 07 Revers | 08 Flash",
                color = Color.White
            )
            Text(text = """  \033[01;03;38;05;147;48;05;21m Текст \033[0m\n""", color = Color.White)
            Text(text = "  Порт 8888 | Очистка экрана \\033[1m ", color = Color.White)

            Spacer(modifier = Modifier.height(5.dp))

            //Рисуем таблицу
            Column(
                Modifier
                    .fillMaxSize(),
                //verticalArrangement = Arrangement.SpaceBetween
            ) {

                for (i in 0..1) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {

                        for (x in 0..7) {

                            Box(
                                Modifier
                                    .padding(start = 0.5.dp, top = 0.5.dp)
                                    .height(boxSize)
                                    .weight(1f)
                                    .background(colorIn256(x + i * 8)),
                                contentAlignment = Alignment.Center
                            )
                            {
                                val textcolor = when (x + i * 8) {
                                    in 0..4, in 16..27, in 232..243 -> Color(0xFFBBBBBB)
                                    else -> Color.Black
                                }

                                Text(
                                    text = "${x + i * 8}",
                                    color = textcolor,
                                    fontSize = textSize,
                                    fontWeight = fontWeight
                                )
                            }
                        }
                    }
                }

                var index = 16

                for (i in 0..14) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {

                        for (x in 0..15) {

                            Box(
                                Modifier
                                    .height(boxSize)
                                    .padding(start = 0.5.dp, top = 0.5.dp)
                                    .weight(1f)
                                    .background(colorIn256(index)),
                                contentAlignment = Alignment.Center
                            )
                            {


                                val textcolor = when (index) {
                                    in 0..4, in 16..27, in 232..243 -> Color(0xFFBBBBBB)
                                    else -> Color.Black
                                }

                                Text(
                                    text = "${index}",
                                    color = textcolor,
                                    fontSize = textSize,
                                    fontWeight = fontWeight
                                )

                                index++
                            }
                        }
                    }
                }
            }




            Spacer(modifier = Modifier.width(50.dp))


        }

        BottomNavigationInfo(navController)


    }
}



