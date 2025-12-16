package com.dominik.control.kidshield.ui.composable.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominik.control.kidshield.ui.controller.DataEvent
import com.dominik.control.kidshield.ui.controller.DataViewModel
import com.dominik.control.kidshield.ui.controller.LoginEvent
import com.dominik.control.kidshield.ui.controller.LoginViewModel
import com.dominik.control.kidshield.utils.formatDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(
    viewModel: DataViewModel,
    onNavigateToHome: () -> Unit
) {

    val list by viewModel.list.observeAsState()
    Log.d("Dev", list.toString())
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadApps()
        viewModel.events.collect { event ->
            when (event) {
                is DataEvent.NavigateToHome -> onNavigateToHome()
                is DataEvent.ShowMessage -> {
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

            LazyColumn {
                if (list != null) {
                    items(list!!) { listItem ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            shadowElevation = 5.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ){

                            Column(modifier = Modifier.padding(16.dp)) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ){
                                    Text(
                                        text = listItem.appName,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = listItem.versionCode.toString()+listItem.versionName,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Text(
                                    text = "Last updated" +": ${formatDateTime(listItem.lastUpdateTime)}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Text(
                                    text = "First installed" +": ${formatDateTime(listItem.firstInstallTime)}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                            }

                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }

        }

    }
}
