package com.example.terminalm3

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.terminalm3.theme.RTTClientM3Theme
import com.example.terminalm3.utils.KeepScreenOn

lateinit var shared: SharedPreferences

var ipAddress: String = "0.0.0.0"

class MainActivity : ComponentActivity() {

    private val vm: VM by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        hideStatusBar()
        if (!Global.isInitialized) Initialization(applicationContext)
        Global.isInitialized = true
        vm.launchUiChannelReceive()

        setContent {
            KeepScreenOn()
            RTTClientM3Theme( darkTheme = false, dynamicColor = false )
            {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    BuildNavGraph()
                }
            }
        }

        // Re-apply after compose content is attached to avoid status bar reappearing on newer Android.
        window.decorView.post { hideStatusBar() }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideStatusBar()
    }

    override fun onResume() {
        super.onResume()
        hideStatusBar()
    }

    private fun hideStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val decorView = window.peekDecorView() ?: return
        WindowCompat.getInsetsController(window, decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
