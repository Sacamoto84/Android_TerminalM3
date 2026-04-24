package com.example.terminalm3.screen.lazy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.terminalm3.Global
import com.example.terminalm3.console
import com.example.terminalm3.lan.TcpCommandSender
import com.siddroid.holi.colors.MaterialColor
import kotlinx.coroutines.launch

@Composable
fun CardEsp32SendControls() {
    val scope = rememberCoroutineScope()
    val host = Global.resolvedServerHost()
    val isHostReady = host != null

    var commandMessage by rememberSaveable { mutableStateOf("") }
    var statusText by rememberSaveable { mutableStateOf("Ожидаю отправку команды") }

    OutlinedCard(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialColor.GREY_900)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "SimpleCLI команды", color = Color.White)
            Text(
                text = if (isHostReady) {
                    "Цель: $host | TCP ${Global.tcpCommandPort} | примеры: help, status, reboot"
                } else {
                    "Сначала укажите IP ESP32 или дождитесь esp.local"
                },
                color = Color(0xFFD0D0D0)
            )

            OutlinedTextField(
                value = commandMessage,
                onValueChange = { commandMessage = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("SimpleCLI команда (TCP 8900)") },
                placeholder = { Text("Например: help") },
                enabled = isHostReady,
                colors = senderTextFieldColors()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = isHostReady && commandMessage.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0),
                        contentColor = Color.White
                    ),
                    onClick = {
                        val payload = commandMessage
                        scope.launch {
                            val result = TcpCommandSender.send(payload)
                            val response = result.getOrNull().orEmpty()
                            statusText = if (result.isSuccess) {
                                response.ifBlank { "Команда отправлена в TCP ${Global.tcpCommandPort}" }
                            } else {
                                "CMD ошибка: ${result.exceptionOrNull()?.message ?: "send failed"}"
                            }

                            console.print(
                                text = if (result.isSuccess) {
                                    "[CMD 8900] $payload"
                                } else {
                                    "[ERR 8900] ${result.exceptionOrNull()?.message ?: "send failed"}"
                                },
                                color = if (result.isSuccess) Color(0xFF90CAF9) else Color(0xFFFF8A80)
                            )

                            if (result.isSuccess && response.isNotBlank()) {
                                console.print(
                                    text = "[CLI 8900] $response",
                                    color = Color(0xFFB3E5FC)
                                )
                            }
                        }
                    }
                ) {
                    Text(text = "Send CMD")
                }
            }

            Text(
                text = statusText,
                color = Color(0xFFBDBDBD)
            )
        }
    }
}

@Composable
private fun senderTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color(0xFF8C8C8C),
    focusedLabelColor = Color(0xFF8FD16A),
    unfocusedLabelColor = Color(0xFFBDBDBD),
    disabledLabelColor = Color(0xFF8C8C8C),
    focusedBorderColor = Color(0xFF8FD16A),
    unfocusedBorderColor = Color(0xFF666666),
    disabledBorderColor = Color(0xFF505050),
    focusedPlaceholderColor = Color(0xFF888888),
    unfocusedPlaceholderColor = Color(0xFF888888),
    disabledPlaceholderColor = Color(0xFF666666)
)
