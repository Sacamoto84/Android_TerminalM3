package com.example.terminalm3.screen.lazy

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.terminalm3.console
import com.example.terminalm3.screen.common.buttons.ButtonClear
import com.example.terminalm3.screen.common.buttons.ButtonSetting
import com.example.terminalm3.screen.common.buttons.ButtonSlegenie
import com.example.terminalm3.screen.lazy.bottomNavigation.ModalBottomSheetContent
import com.example.terminalm3.screen.lazy.bottomNavigation.ModalBottomSheetContentInternal
import com.example.terminalm3.screen.lazy.ui.Warning
import com.example.terminalm3.theme.RTTClientM3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenLazy(navController: NavHostController) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    ScreenLazyInternal(
        scaffoldState = scaffoldState,
        sheetContent = { ModalBottomSheetContent(navController, scaffoldState) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenLazyInternal(
    scaffoldState: BottomSheetScaffoldState,
    sheetContent: @Composable () -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RectangleShape,
        sheetContainerColor = Color.DarkGray,
        containerColor = Color.Black,
        sheetDragHandle = { Row(modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

            ButtonClear()
            ButtonSlegenie()
            ButtonSetting(onClick = {
                //navController.navigate("info")
            })

        } },
        sheetContent = { sheetContent() }
    )
    {
        Box(Modifier.fillMaxSize().padding(bottom = it.calculateBottomPadding()))
        {
            console.lazy()
            Warning()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ScreenLazyPreview() {
    RTTClientM3Theme {
        val scaffoldState = rememberBottomSheetScaffoldState()
        ScreenLazyInternal(
            scaffoldState = scaffoldState,
            sheetContent = {
                ModalBottomSheetContentInternal(
                    isPartiallyExpanded = false,
                    onNavigateToWeb = {}
                )
            }
        )
    }
}
