package com.dominik.control.kidshield.ui.composable.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.dominik.control.kidshield.data.repository.AuthManager
import com.dominik.control.kidshield.data.repository.AuthState
import com.dominik.control.kidshield.ui.composable.screen.DataScreen
import com.dominik.control.kidshield.ui.composable.screen.LoginScreen
import com.dominik.control.kidshield.ui.controller.DataViewModel
import com.dominik.control.kidshield.ui.controller.LoginViewModel

@Composable
fun NavigationStack(
    authManager: AuthManager
)
{
    val navController = rememberNavController()

    // start auth check once
    LaunchedEffect(Unit) {
        authManager.start()
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(route = Screen.Splash.route) {
            val state by authManager.state.collectAsState()
            LaunchedEffect(state) {

                when (state) {
                    is AuthState.Loading -> {
                        // stay
                    }
                    is AuthState.Authenticated -> {
                        navController.navigate(Screen.AppInfo.route) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    is AuthState.Unauthenticated -> {
                        navController.navigate(Screen.Login.route) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            }
            SplashScreen()
        }

        composable(route = Screen.Login.route) { backStackEntry ->
            val viewModel: LoginViewModel = hiltViewModel(backStackEntry)

            LoginScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                navController.navigate(Screen.AppInfo.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        composable(
            route = Screen.AppInfo.route
        ) {backStackEntry ->
            val viewModel: DataViewModel = hiltViewModel(backStackEntry)
            DataScreen(
                viewModel = viewModel,
                onNavigateToHome = { navController.navigate(Screen.Login.route) }
            )
        }

    }
}

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object AppInfo : Screen("appinfo")
}

@Composable
fun SplashScreen() {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
