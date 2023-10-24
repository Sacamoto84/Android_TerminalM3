package com.example.terminalm3

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terminalm3.network.bluetoothAdapter
import com.example.terminalm3.network.btIsReady
import com.example.terminalm3.screen.info.ScreenInfo
import com.example.terminalm3.screen.lazy.ScreenLazy
import com.example.terminalm3.screen.web.Web
import com.example.terminalm3.theme.RTTClientM3Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import timber.log.Timber
import timber.log.Timber.*

lateinit var shared: SharedPreferences

lateinit var ipAddress: String

class MainActivity : ComponentActivity() {

    private val vm: VM by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isInitialized) Initialization(applicationContext)

        isInitialized = true

        setContent {

            KeepScreenOn()

            vm.launchUIChanelReceive()

            RTTClientM3Theme {

                val bluetoothPermissions = // Checks if the device has Android 12 or above
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            rememberMultiplePermissionsState(
                                permissions = listOf(
                                    Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN,
                                    Manifest.permission.BLUETOOTH_CONNECT,
                                    Manifest.permission.BLUETOOTH_SCAN,
                                )
                            )
                        } else {
                            rememberMultiplePermissionsState(
                                permissions = listOf(
                                    Manifest.permission.BLUETOOTH,
                                    Manifest.permission.BLUETOOTH_ADMIN,
                                )
                            )
                        }

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    BuildNavGraph()
                }

                //                if (bluetoothPermissions.allPermissionsGranted) {
                //                    btIsReady
                //                    if (bluetoothAdapter.isEnabled) {
                //
                //                        Surface(
                //                            modifier = Modifier.fillMaxSize(),
                //                            color = MaterialTheme.colorScheme.background
                //                        ) {
                //                            BuildNavGraph(navController)
                //                        }
                //
                //                    } else {
                //
                //                        ButtonBluetooth()
                //
                //                    }
                //                }


            }
        }
    }
}


@Composable
private fun ButtonBluetooth() {
    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        btIsReady = if (it.resultCode == Activity.RESULT_OK) {
            Timber.w("bluetoothLauncher Success")
            true //bluetoothPrint.print()
        } else {
            Timber.w("bluetoothLauncher Failed")
            false
        }

    }

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)


    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
        Button(onClick = {
            if (!bluetoothAdapter.isEnabled) { // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
            }
        }) {
            Text(text = "Включить Bluetooth")
        }
    }


}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BuildNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
        popEnterTransition = { fadeIn(animationSpec = tween(0)) },
        popExitTransition = { fadeOut(animationSpec = tween(0)) },
    ) {

        composable("home") {
            ScreenLazy(navController)
        }

        composable("info") {
            ScreenInfo(navController)
        }

        composable("web") {
            Web(navController)
        }

    }
}



