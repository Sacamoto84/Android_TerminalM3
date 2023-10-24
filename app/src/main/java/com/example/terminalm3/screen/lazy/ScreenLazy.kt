package com.example.terminalm3.screen.lazy

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalDrawer
import androidx.compose.material3.Button
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.terminalm3.console
import com.example.terminalm3.screen.lazy.bottomNavigation.BottomNavigationLazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


val drawerState = MutableStateFlow(DrawerState(DrawerValue.Closed))


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenLazy(navController: NavHostController) {


    val scope = rememberCoroutineScope()

    //val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    //CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

    ModalDrawer(
        drawerContent = {
            //CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                ) {

                    Text(text = "ddddddddd", color = Color.Green)
                    Button(onClick = { /*TODO*/ }) {}
                    Button(onClick = { /*TODO*/ }) {}


                    var checkedIndex by remember { mutableStateOf(0) }
                    val options = listOf(12, 14, 16, 18)

                    SingleChoiceSegmentedButtonRow {
                        options.forEachIndexed { index, resource ->
                            SegmentedButton(
                                selected = index.sp == console.fontSize,
                                onClick = { console.fontSize = index.sp },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index, count = options.size
                                )
                            ) {
                                Text(text = resource.toString())
                            }

                        }


                    }


                }

            //}
        }, drawerState = drawerState.collectAsState().value, gesturesEnabled = true
    ) {


        //CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr)
        //{

        Scaffold(topBar = { Button(onClick = { scope.launch { drawerState.value.open() } }) {} },
            bottomBar = { BottomNavigationLazy(navController) }) {

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = it.calculateBottomPadding())
            ) {
                console.lazy( //Modifier.padding(4.dp).recomposeHighlighter()
                )
                Warning()


            }


        }

        //}


        // }
    }

}



