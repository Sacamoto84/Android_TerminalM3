package com.example.terminalm3.screen.lazy

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.terminalm3.console
import com.example.terminalm3.console.Console
import com.example.terminalm3.screen.common.buttons.ButtonClear
import com.example.terminalm3.screen.common.buttons.ButtonScrollEnd
import com.example.terminalm3.screen.common.buttons.ButtonSetting
import com.example.terminalm3.screen.common.buttons.ButtonSlegenie
import com.example.terminalm3.screen.lazy.bottomNavigation.ModalBottomSheetContent
import com.example.terminalm3.screen.lazy.bottomNavigation.ModalBottomSheetContentInternal
import com.example.terminalm3.screen.lazy.ui.TcpConnectionStatusChip
import com.example.terminalm3.theme.RTTClientM3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenLazy(
    onOpenInfo: () -> Unit,
    onOpenWeb: () -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    ScreenLazyInternal(
        scaffoldState = scaffoldState,
        onOpenInfo = onOpenInfo,
        sheetContent = { ModalBottomSheetContent(scaffoldState, onNavigateToWeb = onOpenWeb) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenLazyInternal(
    scaffoldState: BottomSheetScaffoldState,
    onOpenInfo: () -> Unit = {},
    sheetContent: @Composable () -> Unit
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RectangleShape,
        sheetContainerColor = Color.DarkGray,
        containerColor = Color.Black,
        sheetDragHandle = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp,  end = 8.dp, top = 4.dp)

                ,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonClear(modifier = Modifier.weight(1f))
                ButtonScrollEnd(modifier = Modifier.weight(1f))
                ButtonSlegenie(modifier = Modifier.weight(1f))
                ButtonSetting(
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenInfo() }
                )
            }
        },
        sheetContent = { sheetContent() }
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = it.calculateBottomPadding())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConsoleChannelSelector(
                    modifier = Modifier.weight(1f)
                )
                TcpConnectionStatusChip()
            }
            Box(modifier = Modifier.weight(1f)) {
                console.lazy(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun ConsoleChannelSelector(modifier: Modifier = Modifier) {
    val activeChannel = console.activeChannel
    val channels = rememberChannelIds()

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(channels) { channelId ->
            val selected = channelId == activeChannel
            val unreadCount = console.getUnreadCount(channelId)
            val label = if (channelId == Console.ALL_CHANNEL) "ALL" else channelId.toString()
            Button(
                modifier = Modifier.height(34.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) Color(0xFF4C8A2B) else Color(0xFF2A2A2A),
                    contentColor = if (selected) Color.White else Color(0xFFBFC7CD)
                ),
                onClick = { console.selectChannel(channelId) }
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = Color(0xFFD84315),
                                contentColor = Color.White
                            ) {
                                Text(text = formatUnreadCount(unreadCount))
                            }
                        }
                    }
                ) {
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
private fun rememberChannelIds(): List<Int> {
    return androidx.compose.runtime.remember {
        listOf(Console.ALL_CHANNEL) + List(Console.CHANNEL_COUNT) { it }
    }
}

private fun formatUnreadCount(count: Int): String {
    return if (count > 99) "99+" else count.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ScreenLazyPreview() {
    RTTClientM3Theme {
        val scaffoldState = rememberBottomSheetScaffoldState()
        ScreenLazyInternal(
            scaffoldState = scaffoldState,
            onOpenInfo = {},
            sheetContent = {
                ModalBottomSheetContentInternal(
                    isPartiallyExpanded = false,
                    onNavigateToWeb = {}
                )
            }
        )
    }
}
