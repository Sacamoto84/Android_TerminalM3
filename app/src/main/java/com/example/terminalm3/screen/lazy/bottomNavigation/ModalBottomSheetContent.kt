package com.example.terminalm3.screen.lazy.bottomNavigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.terminalm3.screen.lazy.ui.ButtonPortal
import com.example.terminalm3.screen.lazy.ui.CardFontSize
import com.example.terminalm3.screen.lazy.ui.CardIpAdress
import com.example.terminalm3.screen.lazy.ui.CheckVisibleCRLF
import com.example.terminalm3.screen.lazy.ui.CheckVisibleLineNumber

@Composable
fun ModalBottomSheetContent(navController: NavHostController) {
    Column {

        Canvas(modifier = Modifier.fillMaxWidth().height(4.dp), onDraw = {
            drawLine(
                Color.Gray, Offset(size.width * 0.4f , size.height),
                Offset(size.width * 0.6f, size.height), strokeWidth = 2.dp.toPx())
        })

        CheckVisibleLineNumber()
        CheckVisibleCRLF()
        CardIpAdress()
        ButtonPortal(navController)
        CardFontSize()

    }
}
