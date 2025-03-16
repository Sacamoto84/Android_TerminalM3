package com.example.terminalm3.screen.web

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.terminalm3.R
import com.example.terminalm3.global
import com.example.terminalm3.lan.ping
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "SetJavaScriptEnabled",
    "UnusedMaterialScaffoldPaddingParameter"
)
@Composable
fun ScreenWeb(
    onClickBack: () -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val navigator = WebViewNavigator(coroutineScope)
    var refreshing by remember { mutableStateOf(true) }
    val refreshScope = rememberCoroutineScope()
    val ip = "http://" + global.ipESP.substring(global.ipESP.lastIndexOf('/') + 1)
    val ping = remember { mutableStateOf(ping(ip)) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        println("onRefresh")
        ping.value = ping(ip)
        navigator.reload()
        refreshing = false
    }

    val stateRefresh = rememberPullRefreshState(refreshing, ::refresh)
    val state = rememberWebViewState(ip)

    println("URL $ip")

    //val swipeRefreshState = rememberSwipeRefreshState(false)

    Scaffold(
        modifier = Modifier
            .background(Color.DarkGray)
            ,

        bottomBar = { BottomNavigation(onClickBack) }) {

        //pullRefresh modifier
        Box(
            Modifier.padding(it)
                .fillMaxSize()
                .background(Color.Blue)
            , contentAlignment = Alignment.Center
        ) {




                    Text(
                        text = "Отсутствует связь с $ip",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Green),
                        textAlign = TextAlign.Center
                    )

            PullRefreshIndicator(true, stateRefresh, Modifier)


//            if (ping.value){
//                WebView(
//                    modifier = Modifier
//                        .padding(5.dp)
//                        .border(
//                            width = 5.dp,
//                            color = Color(0xFF6650a4),
//                            shape = RoundedCornerShape(20.dp)
//                        )
//                        .background(Color.Magenta)
//                        //.verticalScroll(rememberScrollState())
//
//                    ,
//                    navigator = navigator,
//                    state = state,
//                    captureBackPresses = false,
//                    onCreated = { webWiew ->
//                        webWiew.settings.javaScriptEnabled = true
//                    }
//                )
//            }
//            else
//                Text(text = "Отсутствует связь с $ip", modifier = Modifier.fillMaxWidth().background(Color.Green), textAlign = TextAlign.Center)

            //BottomNavigation(onClickBack)
            //Spacer(modifier = Modifier.height(8.dp))

            //standard Pull-Refresh indicator. You can also use a custom indicator


        }


    }


}

@Composable
private fun BottomNavigation(onClickBack: () -> Unit) {

    Box(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center,
    )
    {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier
                    .height(34.dp)
                    .fillMaxWidth()
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF505050)),
                onClick = onClickBack
            )
            {
                Icon(
                    painter = painterResource(R.drawable.back1),
                    tint = Color.LightGray,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

        }
    }
}