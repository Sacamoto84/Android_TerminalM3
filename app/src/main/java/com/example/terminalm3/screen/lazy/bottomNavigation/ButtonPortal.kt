package com.example.terminalm3.screen.lazy.bottomNavigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ButtonPortal(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    Button(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, start = 8.dp, end = 8.dp),
        onClick = {
            scope.launch(Dispatchers.Main) {
                navController.navigate("web")
            }
        }) {
        Text("Открыть Портал", fontSize = 20.sp)
    }
}