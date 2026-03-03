package com.dominik.control.kidshield.ui.composable.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dominik.control.kidshield.ui.controller.PairingEvent
import com.dominik.control.kidshield.ui.controller.PairingRole
import com.dominik.control.kidshield.ui.controller.PairingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairingScreen(
    viewModel: PairingViewModel,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PairingEvent.NavigateToHome -> onNavigateToHome()
                is PairingEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.msg)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text("Kid Shield", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ustawienia", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        }
    ) { paddingValues->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (state.role == PairingRole.PARENT) {
                // MONITORED VIEW
                Text("Enter code from child's device", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.inputPin,
                    onValueChange = { viewModel.updateInputPin(it) },
                    label = { Text("PIN Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.pair() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.loading
                ) {
                    if (state.loading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Pair Device")
                }
            } else {
                // SUPERVISOR VIEW
                Text("Pair New Parent Device", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(32.dp))

                if (state.generatedPin == null) {
                    Button(onClick = { viewModel.generatePin() }) {
                        Text("Generate Pairing Code")
                    }
                } else {
                    Text("Display this code on parent's device:")
                    Text(
                        text = state.generatedPin!!,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Waiting for parent to connect...", style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }


//            Text(
//                text = "Pair",
//                fontSize = 32.sp
//            )
//
//            Spacer(Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = state.pin,
//                onValueChange = { viewModel.updatePin(it) },
//                label = { Text("PIN", color = MaterialTheme.colorScheme.onBackground) },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(16.dp))
//
//
//        Button(
//            modifier = Modifier.fillMaxWidth(),
//            onClick = {
//                    viewModel.generatePin()
//
//            },
//            enabled = !state.loading
//        ) {
//            if (state.loading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(20.dp),
//                    strokeWidth = 2.dp
//                )
//                Spacer(Modifier.width(8.dp))
//            }
//
//            Text(
//                text = "Generate Pin",
//                fontSize = 16.sp
//            )
//        }
//
//            Spacer(Modifier.height(8.dp))

        }

    }
}
