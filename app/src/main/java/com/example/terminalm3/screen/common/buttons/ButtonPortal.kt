package com.example.terminalm3.screen.common.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Preview(apiLevel = 31)
@Composable
private fun Preview(){
    ButtonPortal()
}

@Composable
fun ButtonPortal(
    //navController: NavHostController
    onClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    Button(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        onClick = {
            scope.launch(Dispatchers.Main) {
                //navController.navigate("web")
                onClick()
            }
        }) {
        Text("Открыть Портал", fontSize = 20.sp)
    }
}