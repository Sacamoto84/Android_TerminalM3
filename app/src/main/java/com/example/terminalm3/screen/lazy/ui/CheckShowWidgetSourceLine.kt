package com.example.terminalm3.screen.lazy.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import com.example.terminalm3.Global
import com.example.terminalm3.shared

@Composable
fun CheckShowWidgetSourceLine() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
    ) {
        Checkbox(
            checked = Global.showWidgetSourceLine,
            onCheckedChange = {
                Global.showWidgetSourceLine = it
                shared.edit { putBoolean("showWidgetSourceLine", it) }
            },
            colors = CheckboxDefaults.colors(uncheckedColor = Color.LightGray)
        )
        Text(text = "Показывать строку команды виджета", color = Color.White)
    }
}
