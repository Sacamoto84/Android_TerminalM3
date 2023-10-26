package com.example.terminalm3.screen.lazy

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.terminalm3.console
import com.example.terminalm3.screen.lazy.bottomNavigation.BottomNavigationLazy
import com.example.terminalm3.screen.lazy.bottomNavigation.ModalBottomSheetContent
import com.example.terminalm3.screen.lazy.ui.ButtonPortal
import com.example.terminalm3.screen.lazy.ui.CardFontSize
import com.example.terminalm3.screen.lazy.ui.CardIpAdress
import com.example.terminalm3.screen.lazy.ui.CheckVisibleCRLF
import com.example.terminalm3.screen.lazy.ui.CheckVisibleLineNumber
import kotlinx.coroutines.flow.MutableStateFlow


val isSheetOpen = MutableStateFlow(false)

val isDialogOpen = MutableStateFlow(false)

@OptIn(ExperimentalMaterial3Api::class)
val sheetState = MutableStateFlow(SheetState(false, SheetValue.Expanded))


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenLazy(navController: NavHostController) {


    val scope = rememberCoroutineScope()

    //val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    //CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

    //    ModalDrawer(
    //        drawerContent = {
    //            //CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
    //                Column(
    //                    modifier = Modifier
    //                        .fillMaxSize()
    //                        .background(Color.Gray)
    //                ) {
    //
    //                    Text(text = "ddddddddd", color = Color.Green)
    //                    Button(onClick = { /*TODO*/ }) {}
    //                    Button(onClick = { /*TODO*/ }) {}
    //
    //
    //                    var checkedIndex by remember { mutableStateOf(0) }
    //                    val options = listOf(12, 14, 16, 18)
    //
    //                    SingleChoiceSegmentedButtonRow {
    //                        options.forEachIndexed { index, resource ->
    //                            SegmentedButton(
    //                                selected = index.sp == console.fontSize,
    //                                onClick = { console.fontSize = index.sp },
    //                                shape = SegmentedButtonDefaults.itemShape(
    //                                    index = index, count = options.size
    //                                )
    //                            ) {
    //                                Text(text = resource.toString())
    //                            }
    //
    //                        }
    //
    //
    //                    }
    //
    //
    //                }
    //
    //            //}
    //        }, drawerState = drawerState.collectAsState().value, gesturesEnabled = true
    //    )
    //{


    //CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr)
    //{


    //val sheetState = rememberModalBottomSheetState()


    val scaffoldState = rememberBottomSheetScaffoldState(

        bottomSheetState = SheetState(
            false, initialValue = SheetValue.PartiallyExpanded, skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        sheetShape = androidx.compose.ui.graphics.RectangleShape,
        sheetContainerColor = Color.DarkGray,
        sheetPeekHeight = 58.dp,
        scaffoldState = scaffoldState,
        sheetDragHandle = { BottomNavigationLazy(navController) },
        sheetSwipeEnabled = true,
        sheetContent = { ModalBottomSheetContent(navController) }) {



        console.lazy(
            Modifier
                .padding(4.dp, bottom = it.calculateBottomPadding())
                //.recomposeHighlighter()
        )



        //        Scaffold(modifier = Modifier.padding(bottom = it.calculateBottomPadding()), topBar = {
        //
        //            Button(onClick = {
        //                scope.launch { isSheetOpen.value = true }
        //            }) {}
        //
        //        },
        //            bottomBar = { BottomNavigationLazy(navController) }) {
        //            Box(
        //                Modifier
        //                    .fillMaxSize()
        //                    .padding(bottom = it.calculateBottomPadding())
        //            ) {
        //                console.lazy( //Modifier.padding(4.dp).recomposeHighlighter()
        //                )
        //                Warning()
        //            }
        //
        //
        //        }


    }

}



