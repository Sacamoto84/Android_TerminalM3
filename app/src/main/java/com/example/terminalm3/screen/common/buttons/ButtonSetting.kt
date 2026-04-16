package com.example.terminalm3.screen.common.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.terminalm3.R

@Preview(apiLevel = 31, showBackground = true)
@Composable
private fun Preview(){
    ButtonSetting()
}

@Composable
fun ButtonSetting(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF505050)),
        onClick = {
            //navController.navigate("info")
            onClick()
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.settings1),
            tint = Color.LightGray,
            contentDescription = "Settings"
        )
    }
}
