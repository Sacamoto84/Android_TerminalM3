package com.example.terminalm3.screen.lazy.bottomNavigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.terminalm3.isCheckedUseLiteralEnter
import com.example.terminalm3.shared

@Composable
fun CheckVisibleCRLF() {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 10.dp) //modifier = Modifier.background(Color.Red)
    ) {

        Checkbox(
            checked = isCheckedUseLiteralEnter, onCheckedChange = {
                isCheckedUseLiteralEnter = it
                shared.edit().putBoolean("enter", it).apply()
            }, colors = CheckboxDefaults.colors(uncheckedColor = Color.LightGray)
        )

        Text(text = "Вывести символ CR LF", color = Color.White)
    }

}