package com.dominik.control.kidshield

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.dominik.control.kidshield.data.repository.AuthManager
import com.dominik.control.kidshield.ui.composable.navigation.NavigationStack
import com.dominik.control.kidshield.ui.controller.PermissionManager
import com.dominik.control.kidshield.ui.theme.KidShieldTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class KidShield : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() {
            Log.d("WM", "Using HiltWorkerFactory")
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(Log.INFO)
                .build()
        }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authManager: AuthManager
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(this)

        enableEdgeToEdge()
        setContent {

            KidShieldTheme {
                NavigationStack(authManager, permissionManager)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        permissionManager.refresh()
    }

}
