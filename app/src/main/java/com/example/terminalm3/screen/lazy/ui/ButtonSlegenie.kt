package com.example.terminalm3.screen.lazy.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.terminalm3.console

@Composable
fun ButtonSlegenie(modifier: Modifier = Modifier) {

    BadgedBox(badge = {
        Badge {
            Text(console.messages.size.toString())
        }
    }) {

        Icon(Icons.Filled.MoreVert, contentDescription = "")
    }

    //По кнопке включаем слежение
    //val slegenie by telnetSlegenie.observeAsState()

    // Кнопка включения слежения
    //    Button(
    //        modifier = Modifier
    //            .fillMaxHeight()
    //            .fillMaxWidth()
    //            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
    //            .then(modifier),
    //        colors = ButtonDefaults.buttonColors(
    //            containerColor = if (slegenie == true) Color(0xFF8AAF4A) else Color.DarkGray
    //        ),
    //        onClick = {
    //            telnetSlegenie.value = !telnetSlegenie.value!!
    //            console.lastCount = console._messages.size
    //            console.tracking = telnetSlegenie.value!!
    //        }
    //    ) {
    //        //console.recompose()
    //        //Text(text = "${ console.messages.value.size }")
    //    }
}