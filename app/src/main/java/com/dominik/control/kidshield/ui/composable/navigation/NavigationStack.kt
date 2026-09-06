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
import com.dominik.control.kidshield.ui.composable.screen.PairingScreen
import com.dominik.control.kidshield.ui.composable.screen.PermissionScreen
import com.dominik.control.kidshield.ui.composable.screen.SettingsScreen
import com.dominik.control.kidshield.ui.controller.DataViewModel
import com.dominik.control.kidshield.ui.controller.LoginViewModel
import com.dominik.control.kidshield.ui.controller.PairingViewModel
import com.dominik.control.kidshield.ui.controller.PermissionManager
import com.dominik.control.kidshield.ui.controller.PermissionViewModel
import com.dominik.control.kidshield.ui.controller.SettingsViewModel

@Composable
fun NavigationStack(
    authManager: AuthManager,
    permissionManager: PermissionManager
)
{
    val navController = rememberNavController()
    val authState by authManager.state.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // start auth check once
    LaunchedEffect(Unit) {
        authManager.start()
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
//    NavHost(navController = navController, startDestination = Screen.Pairing.route) {

        composable(route = Screen.Splash.route) {

            LaunchedEffect(authState) {

                when (authState) {
                    is AuthState.Loading -> {
                        // stay
                    }
                    is AuthState.Authenticated -> {
                        navController.navigate(Screen.Permissions.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                    is AuthState.Unauthenticated -> {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            }
            SplashScreen()
        }

        composable(
            route = Screen.Login.route
        ) { backStackEntry ->
            val viewModel: LoginViewModel = hiltViewModel(backStackEntry)

            LoginScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                navController.navigate(Screen.Permissions.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        composable(
            route = Screen.Pairing.route
        ) {backStackEntry ->
            val viewModel: PairingViewModel = hiltViewModel(backStackEntry)

            PairingScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Permissions.route){
                        popUpTo(Screen.Permissions.route) { inclusive = true }
                    }
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.Permissions.route
        ) {backStackEntry ->
            val viewModel = hiltViewModel<PermissionViewModel, PermissionViewModel.Factory>(
                creationCallback = { factory -> factory.create(permissionManager = permissionManager) }
            )

            PermissionScreen(
                viewModel = viewModel,
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Settings.route) { backStackEntry ->
            val viewModel: SettingsViewModel = hiltViewModel(backStackEntry)

            SettingsScreen(
                viewModel = viewModel,
                onNavigateToPairing = {
                    navController.navigate(Screen.Pairing.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
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
    data object Permissions : Screen("permissions")
    data object Pairing : Screen("pairing")
    data object Settings : Screen("settings")
}

@Composable
fun SplashScreen() {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
