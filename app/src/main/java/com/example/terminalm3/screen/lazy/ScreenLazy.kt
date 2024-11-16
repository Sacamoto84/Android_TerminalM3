package com.example.terminalm3.screen.lazy

import android.annotation.SuppressLint
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.NavHostController
import com.example.terminalm3.screen.lazy.bottomNavigation.ModalBottomSheetContent

var isConfimChange = mutableStateOf(false)


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenLazy(navController: NavHostController) {

    val scaffoldState = rememberBottomSheetScaffoldState(

        bottomSheetState = SheetState(
            skipPartiallyExpanded = false, // pass false here
            initialValue = SheetValue.PartiallyExpanded,

            density = LocalDensity.current,
            confirmValueChange = { true },
            skipHiddenState = false
        )

    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        //sheetShape = androidx.compose.ui.graphics.RectangleShape,
        //sheetContainerColor = Color.DarkGray,
        //containerColor = Color.Black,
        //sheetPeekHeight = 58.dp,

        //sheetDragHandle = {
        //    BottomNavigationLazy(navController)
        //},
        // = true,
        sheetContent = { ModalBottomSheetContent(navController, scaffoldState)
}

    )
    {

//        Box(
//            Modifier
//                .fillMaxSize()
//                .padding(bottom = it.calculateBottomPadding())
//        ) {
//            console.lazy() //Modifier.padding(4.dp).recomposeHighlighter()
//            Warning()
//        }

    }

}



