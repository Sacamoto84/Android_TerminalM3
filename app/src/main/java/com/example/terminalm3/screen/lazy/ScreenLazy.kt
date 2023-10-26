package com.example.terminalm3.screen.lazy

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.terminalm3.console
import com.example.terminalm3.screen.lazy.bottomNavigation.BottomNavigationLazy
import com.example.terminalm3.screen.lazy.bottomNavigation.colorBg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import libs.modifier.recomposeHighlighter


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
            false,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        sheetContainerColor = Color.DarkGray,
        sheetPeekHeight = 50.dp,
        scaffoldState = scaffoldState,
        sheetDragHandle = {BottomNavigationLazy(navController)},
        sheetSwipeEnabled = true,
                sheetContent = {ModalBottomSheetContent(navController)}) {

        console.lazy(Modifier.padding(4.dp, bottom = it.calculateBottomPadding()).recomposeHighlighter())

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






//    if (isSheetOpen.collectAsState().value) {
//        ModalBottomSheet(
//            onDismissRequest = {
//                //isSheetOpen.value = false
//                               },
//            sheetState = sheetState.collectAsState().value,
////dragHandle = {}
//
//        ) {
//
//        }
//    }




    //    BottomSheetDialog(
    //        onDismissRequest = { //show =
    //            false
    //        }, properties = BottomSheetDialogProperties(
    //            behaviorProperties = BottomSheetBehaviorProperties(
    //                expandedOffset = 40,
    //                isHideable = true,
    //                isFitToContents = false,
    //                //isGestureInsetBottomIgnored = true
    //            )
    //        )
    //    ) { // content
    //        Surface {
    //            Column {
    //
    //                Text(text = "ddddddddd", color = Color.Green)
    //                Button(onClick = { /*TODO*/ }) {}
    //                Button(onClick = { /*TODO*/ }) {}
    //            }
    //        }
    //    }


    ////////////////////////////
    //    Scaffold(topBar = { Button(onClick = { scope.launch { drawerState.value.open() } }) {} },
    //        bottomBar = { BottomNavigationLazy(navController) }) {
    //        Box(
    //            Modifier
    //                .fillMaxSize()
    //                .padding(bottom = it.calculateBottomPadding())
    //        ) {
    //            console.lazy( //Modifier.padding(4.dp).recomposeHighlighter()
    //            )
    //            Warning()
    //        }
    //    }
    /////////////////////////////
    //}


    // }
    //}

}


@Composable
fun ModalBottomSheetContent(navController: NavHostController)
{
    Column {
        OpenPortal(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenPortal(navController: NavHostController)
{

    val scope = rememberCoroutineScope()

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, start = 8.dp, end = 8.dp),
        onClick = {
            scope.launch(Dispatchers.Main) {
                navController.navigate("web")
            }
        }
    )
    {
        Text("Открыть Портал", fontSize = 20.sp)
    }
}