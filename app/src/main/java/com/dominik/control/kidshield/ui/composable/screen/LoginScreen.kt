package com.dominik.control.kidshield.ui.composable.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import com.dominik.control.kidshield.ui.composable.components.UserTypeSelector
import com.dominik.control.kidshield.ui.controller.LoginEvent
import com.dominik.control.kidshield.ui.controller.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> onNavigateToHome()
                is LoginEvent.ShowMessage -> {
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
                .padding(16.dp),
        ) {
            Text(
                text = if (!state.registerScreen) "Log in" else "Register",
                fontSize = 32.sp
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.username,
                onValueChange = { viewModel.updateLogin(it) },
                label = { Text("Login", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Password", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            if (state.registerScreen) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Account Type",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(8.dp))

                UserTypeSelector(
                    selected = state.userType,
                    onSelected = viewModel::updateUserType,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            @OptIn(ExperimentalAnimationApi::class)
            AnimatedContent(
                targetState = state.registerScreen,
                transitionSpec = {
                    (slideInVertically { it } + fadeIn()) with
                            (slideOutVertically { -it } + fadeOut())
                },
                label = "auth_button"
            ) { mode ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (!mode) {
                            viewModel.login()
                        } else {
                            viewModel.register()
                        }
                    },
                    enabled = !state.loading
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Text(
                        text = if (mode == false) "Log in" else "Register",
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = {
                    viewModel.toggleScreen()
                }
            ) {
                Text(
                    text = if (!state.registerScreen) {
                        "Don't have account? Register"
                    } else {
                        "Already have an account? Log in"
                    }
                )
            }

        }

    }
}
