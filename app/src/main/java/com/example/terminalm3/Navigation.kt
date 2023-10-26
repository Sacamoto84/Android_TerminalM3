package com.example.terminalm3

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.terminalm3.screen.info.ScreenInfo
import com.example.terminalm3.screen.lazy.ScreenLazy
import com.example.terminalm3.screen.web.Web

@Composable
fun BuildNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { fadeIn(animationSpec = tween(2000)) },
        exitTransition = { fadeOut(animationSpec = tween(2000)) },
        popEnterTransition = { fadeIn(animationSpec = tween(200)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) },
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