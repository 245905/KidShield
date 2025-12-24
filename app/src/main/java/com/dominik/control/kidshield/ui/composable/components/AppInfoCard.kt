package com.dominik.control.kidshield.ui.composable.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominik.control.kidshield.data.model.domain.AppChangeType
import com.dominik.control.kidshield.utils.AppUiModel
import com.dominik.control.kidshield.utils.formatDateTime

private data class Quad<A,B,C,D>(val a:A, val b:B, val c:C, val d:D)

@Composable
fun AppInfoCard(
    model: AppUiModel,
    onClick: () -> Unit
) {
    val addedColor = Color(0xFF2E7D32)   // zielony
    val removedColor = Color(0xFFB00020) // czerwony
    val updatedColor = Color(0xFF1565C0) // niebieski
    val neutralBar = MaterialTheme.colorScheme.surfaceVariant

    val change = model.diff?.changeType

    // pick color & icon
    val (barColor, icon, iconTint, changeLabel) = when (change) {
        AppChangeType.ADDED -> Quad(addedColor, Icons.Default.Add, addedColor, "Installed")
        AppChangeType.REMOVED -> Quad(removedColor, Icons.Default.Delete, removedColor, "Removed")
        AppChangeType.UPDATED -> Quad(updatedColor, Icons.Default.Update, updatedColor, "Updated")
        else -> Quad(neutralBar, null, neutralBar, null)
    }

    // subtle background based on change
    val background = when (change) {
        AppChangeType.ADDED -> addedColor.copy(alpha = 0.08f)
        AppChangeType.REMOVED -> removedColor.copy(alpha = 0.08f)
        AppChangeType.UPDATED -> updatedColor.copy(alpha = 0.06f)
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    // animated color for left bar (subtle)
    val animatedBarColor by animateColorAsState(targetValue = barColor)

    Surface(
        tonalElevation = 3.dp,
        shape = MaterialTheme.shapes.medium,
        color = background,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // left color bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(animatedBarColor)
            )

            Column(modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = model.info?.appName ?: model.diff?.appName ?: "(lack of name)",
                            fontSize = 25.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = model.info?.packageName ?: model.diff?.packageName ?: "",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // change icon + label (if any)
                    if (icon != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Icon(
                                imageVector = icon,
                                contentDescription = changeLabel,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(text = changeLabel ?: "", fontSize = 15.sp, color = iconTint)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // versions
                val versionText = buildString {
                    val newName = model.info?.versionName
                    val newCode = model.info?.versionCode
                    val oldName = model.diff?.oldVersionName
                    val oldCode = model.diff?.oldVersionCode

                    when (model.diff?.changeType) {
                        AppChangeType.UPDATED -> {
                            append("Version: ")
                            append(oldName ?: "n/d")
                            append(" (${oldCode})")
                            append(" -> ")
                            append(newName ?: "n/d")
                            append(" (${newCode})")
                        }
                        AppChangeType.ADDED -> {
                            append("Version: ")
                            append(newName ?: "n/d")
                            append(" (${newCode})")
                        }
                        AppChangeType.REMOVED -> {
                            append("Version: ")
                            append(oldName ?: "n/d")
                            append(" (${oldCode})")
                        }
                        else -> {
                            append("Version: ")
                            append(newName ?: "n/d")
                            append(" (${newCode})")
                        }
                    }
                }

                Text(text = versionText, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)

                Spacer(Modifier.height(6.dp))

                // dates
                val first = model.info?.firstInstallTime ?: model.diff?.lastUpdateTime ?: 0L
                val last = model.info?.lastUpdateTime ?: model.diff?.lastUpdateTime ?: 0L

                Text(text = "Installed: ${formatDateTime(first)}", fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(text = "Last updated: ${formatDateTime(last)}", fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}
