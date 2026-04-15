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
import com.example.terminalm3.screen.web.ScreenWeb

object AppRoute {
    const val Home = "home"
    const val Info = "info"
    const val Web = "web"
}

@Composable
fun BuildNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(200)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) },
    ) {
        composable(AppRoute.Home) {
            ScreenLazy(
                onOpenInfo = { navController.navigate(AppRoute.Info) },
                onOpenWeb = { navController.navigate(AppRoute.Web) }
            )
        }
        composable(AppRoute.Info) {
            ScreenInfo(onBack = { navController.popBackStack() })
        }
        composable(AppRoute.Web) {
            ScreenWeb(onBack = { navController.popBackStack() })
        }
    }
}
