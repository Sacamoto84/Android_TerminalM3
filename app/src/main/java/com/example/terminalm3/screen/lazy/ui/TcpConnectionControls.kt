package com.example.terminalm3.screen.lazy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.terminalm3.Global
import com.example.terminalm3.TcpConnectionStage
import com.example.terminalm3.lan.TcpBridgeClient
import com.example.terminalm3.shared
import com.siddroid.holi.colors.MaterialColor

@Composable
fun CardServerConnection() {
    val tcpState by Global.tcpConnectionState.collectAsState()
    val effectiveHost = Global.resolvedServerHost()

    OutlinedCard(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialColor.GREY_900)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "TCP сервер ESP32", color = Color.White)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = Global.isServerIpAuto,
                    onCheckedChange = {
                        Global.isServerIpAuto = it
                        shared.edit { putBoolean("serverAutoIp", it) }
                        TcpBridgeClient.requestReconnect("server ip mode changed")
                    },
                    colors = CheckboxDefaults.colors(uncheckedColor = Color.LightGray)
                )
                Text(
                    text = "Авто IP сервера из esp.local",
                    color = Color.White
                )
            }

            OutlinedTextField(
                value = Global.manualServerIp,
                onValueChange = {
                    Global.manualServerIp = it
                    shared.edit { putString("serverManualIp", it) }
                },
                enabled = !Global.isServerIpAuto,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("IP сервера") },
                placeholder = { Text("192.168.0.50") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                colors = OutlinedTextFieldDefaults.colors(
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
            )

            Text(
                text = if (effectiveHost == null) {
                    "Текущий адрес сервера: не найден"
                } else {
                    "ESP32: $effectiveHost | read ${Global.tcpServerPort} | cmd ${Global.tcpCommandPort}"
                },
                color = Color(0xFFD0D0D0)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(connectionStageColor(tcpState.stage), CircleShape)
                )
                Column {
                    Text(
                        text = "Состояние: ${connectionStageLabel(tcpState.stage)}",
                        color = Color.White
                    )
                    if (tcpState.detail.isNotBlank()) {
                        Text(
                            text = tcpState.detail,
                            color = Color(0xFFBBBBBB)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TcpConnectionStatusChip(modifier: Modifier = Modifier) {
    val tcpState by Global.tcpConnectionState.collectAsState()

    Box(
        modifier = modifier
            .size(14.dp)
            .background(connectionStageColor(tcpState.stage), CircleShape)
    )
}

fun connectionStageColor(stage: TcpConnectionStage): Color {
    return when (stage) {
        TcpConnectionStage.Idle -> Color(0xFFBDBDBD)
        TcpConnectionStage.WaitingAddress -> Color(0xFFFFC400)
        TcpConnectionStage.Connecting -> Color(0xFF40C4FF)
        TcpConnectionStage.Connected -> Color(0xFF00E676)
        TcpConnectionStage.Error -> Color(0xFFFF1744)
    }
}

fun connectionStageLabel(stage: TcpConnectionStage): String {
    return when (stage) {
        TcpConnectionStage.Idle -> "Остановлено"
        TcpConnectionStage.WaitingAddress -> "Жду адрес"
        TcpConnectionStage.Connecting -> "Подключаюсь"
        TcpConnectionStage.Connected -> "Подключено"
        TcpConnectionStage.Error -> "Ошибка"
    }
}
