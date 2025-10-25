package com.example.terminalm3.screen.lazy

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation.NavHostController
import com.example.terminalm3.console
import com.example.terminalm3.screen.common.buttons.ButtonSlegenie
import com.example.terminalm3.screen.lazy.bottomNavigation.ModalBottomSheetContent
import com.example.terminalm3.screen.lazy.ui.Warning

var isConfimChange = mutableStateOf(false)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenLazy(navController: NavHostController) {

    val scaffoldState = rememberBottomSheetScaffoldState(

//        bottomSheetState = SheetState(
//            skipPartiallyExpanded = false, // pass false here
//            initialValue = SheetValue.PartiallyExpanded,
//            density = LocalDensity.current,
//            confirmValueChange = { true },
//            skipHiddenState = true,
//            positionalThreshold = TODO(),
//            velocityThreshold = TODO()
//        )

    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RectangleShape,
        sheetContainerColor = Color.DarkGray,
        containerColor = Color.Black,
        //sheetPeekHeight = 58.dp,

        //sheetDragHandle = {
        //    BottomNavigationLazy(navController)
        //},
        // = true,

        sheetDragHandle = { Row{ ButtonSlegenie() } },

        sheetContent = {
            ModalBottomSheetContent(navController, scaffoldState)
        }

    )
    {
        Box( Modifier.fillMaxSize().padding(bottom = it.calculateBottomPadding()) )
        {
            console.lazy() //Modifier.padding(4.dp).recomposeHighlighter()
            Warning()
        }

    }

}



