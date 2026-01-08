package com.dominik.control.kidshield.ui.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dominik.control.kidshield.ui.composable.components.PermissionCard
import com.dominik.control.kidshield.ui.controller.PermissionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel,
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.permissionManager.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        // if some permission is permanently denied, show a short snackbar
        if (viewModel.isPermissionDeniedPermanently()) {
            snackbarHostState.showSnackbar("Niektóre uprawnienia wymagają otwarcia ustawień.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kid Shield", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    IconButton(onClick = { /* open settings screen */ }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Ustawienia",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { p ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(p)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Uprawnienia",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Aby aplikacja działała poprawnie, prosimy o przyznanie poniższych uprawnień.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            PermissionCard(
                icon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White) },
                title = "Lokalizacja (dokładna)",
                subtitle = "Wymagana do śledzenia pozycji dziecka",
                status = state.fineLocation,
                onRequest = { viewModel.requestForegroundLocation() },
                onOpenSettings = { viewModel.openAppSettings() }
            )

            PermissionCard(
                icon = { Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White) },
                title = "Lokalizacja w tle",
                subtitle = "Pozwala na ciągłe śledzenie gdy aplikacja jest wyłączona",
                status = state.backgroundLocation,
                onRequest = { viewModel.requestBackgroundLocation() },
                onOpenSettings = { viewModel.openAppSettings() }
            )

            PermissionCard(
                icon = { Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = Color.White) },
                title = "Rozpoznawanie aktywności",
                subtitle = "Pomaga rozróżnić ruch/stan postoju (oszczędność baterii)",
                status = state.activityRecognition,
                onRequest = { viewModel.requestActivityRecognition() },
                onOpenSettings = { viewModel.openAppSettings() }
            )

            PermissionCard(
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White) },
                title = "Statystyki użycia aplikacji",
                subtitle = "Potrzebne do monitorowania użycia aplikacji przez dziecko",
                status = state.usageStats,
                onRequest = { viewModel.openUsageStatsSettings() },
                onOpenSettings = { viewModel.openUsageStatsSettings() }
            )

            PermissionCard(
                icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White) },
                title = "Wysyłanie powiadomień",
                subtitle = "Potrzebne do wyświetlania statusu śledzenia",
                status = state.notifications,
                onRequest = { viewModel.requestPostNotificationsSettings() },
                onOpenSettings = { viewModel.openAppSettings() }
            )

            if (viewModel.areAllPermissionsGranted()) {
                Button(
                    onClick = {
                        // start tracking / navigate home
//                        onNavigateToHome()
                        viewModel.onStartFullControlClicked()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Text("  Rozpocznij śledzenie", modifier = Modifier.padding(start = 6.dp))
                }
            } else {
                Text(
                    "Aby rozpocząć śledzenie, prosimy przyznać wymagane uprawnienia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
