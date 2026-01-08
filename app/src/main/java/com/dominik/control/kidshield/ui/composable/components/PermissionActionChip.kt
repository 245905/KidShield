package com.dominik.control.kidshield.ui.composable.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dominik.control.kidshield.ui.controller.PermissionStatus

@Composable
fun PermissionActionChip(
    status: PermissionStatus,
    onRequest: () -> Unit,
    onOpenSettings: () -> Unit
) {
    when (status) {
        PermissionStatus.GRANTED -> {
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text("Przyznane") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFFE8F5E9),
                    labelColor = Color(0xFF2E7D32)
                )
            )
        }

        PermissionStatus.DENIED -> {
            AssistChip(
                onClick = onRequest,
                label = { Text("Zezwól") },
                leadingIcon = {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }

        PermissionStatus.DENIED_PERMANENTLY -> {
            AssistChip(
                onClick = onOpenSettings,
                label = { Text("Ustawienia") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    }
}
