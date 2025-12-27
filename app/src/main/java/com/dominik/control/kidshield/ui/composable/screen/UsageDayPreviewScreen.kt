package com.dominik.control.kidshield.ui.composable.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

// --- helper formatting
private fun formatDurationMs(ms: Long): String {
    if (ms <= 0L) return "0s"
    var seconds = ms / 1000
    val hours = seconds / 3600
    seconds -= hours * 3600
    val minutes = seconds / 60
    seconds -= minutes * 60
    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        if (hours == 0L && minutes == 0L) append("${seconds}s")
    }
}

@Suppress("SimpleDateFormat")
private fun dateShort(d: Date) = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(d)

// --- lightweight preview models (match your entities shape)
data class UsagePreview(
    val appName: String,
    val packageName: String,
    val totalTimeInForeground: Long
)

data class HourlyPreview(
    val hour: Int,
    val packageName: String?, // null means phone-wide aggregation
    val totalTime: Long
)

// --- Sample fixed data
private fun sampleUsage(): List<UsagePreview> {
    val now = System.currentTimeMillis()
    return listOf(
        UsagePreview("YouTube", "com.google.android.youtube", 1000L * 60 * 90), // 90 min
        UsagePreview("Chrome", "com.android.chrome", 1000L * 60 * 40),
        UsagePreview("Spotify", "com.spotify.music", 1000L * 60 * 10),
        UsagePreview("Messenger", "com.facebook.orca", 1000L * 60 * 7)
    )
}

private fun sampleHourly(): List<HourlyPreview> {
    val out = mutableListOf<HourlyPreview>()
    // phone-wide aggregates (packageName = null)
    (0..23).forEach { h ->
        val base = when {
            h in 7..9 -> 10L * 60 * 1000L
            h in 18..21 -> 50L * 60 * 1000L
            else -> (h % 5) * 4L * 60 * 1000L
        }
        out += HourlyPreview(h, null, base)
    }
    // YouTube concentrated evening
    (0..23).forEach { h ->
        val t = if (h in 18..20) 25L * 60 * 1000L else (h % 6) * 2L * 60 * 1000L
        out += HourlyPreview(h, "com.google.android.youtube", t)
    }
    // Chrome morning
    (0..23).forEach { h ->
        val t = if (h in 8..11) 15L * 60 * 1000L else (h % 4) * 3L * 60 * 1000L
        out += HourlyPreview(h, "com.android.chrome", t)
    }
    // Spotify small
    (0..23).forEach { h ->
        val t = if (h in 6..9) 10L * 60 * 1000L else (h % 3) * 60 * 1000L
        out += HourlyPreview(h, "com.spotify.music", t)
    }
    return out
}

/** --- MAIN SCREEN (hardcoded preview) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageDayPreviewScreen() {
    val date = Date()
    val usage = remember { sampleUsage() }
    val hourly = remember { sampleHourly() }

    // phone-wide hourly (packageName == null)
    val phoneHourly = hourly.filter { it.packageName == null }
    // map each app to its hourly entries
    val apps = usage

    var selectedDate by remember { mutableStateOf(Date()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Podsumowanie — ${dateShort(date)}", color = MaterialTheme.colorScheme.onBackground) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            DaySelector(
                date = selectedDate,
                onDateChange = { selectedDate = it }
            )

            Spacer(Modifier.height(12.dp))

            // SUMMARY CARDS
            SummaryRow(usage)

            // HERO TIMELINE CARD
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Aktywność w ciągu dnia", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Timeline - phone-wide
                    HourTimeline(
                        hours = phoneHourly.map { it.totalTime },
                        height = 44.dp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("6", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("12", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("18", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("24", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // APPS LIST with mini-timeline
            Text("Aplikacje", fontWeight = FontWeight.SemiBold)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                items(apps) { app ->
                    val appHourly = hourly.filter { it.packageName == app.packageName }.map { it.totalTime }
                    AppListRow(appName = app.appName, total = app.totalTimeInForeground, hours = appHourly)
                }
            }
        }
    }
}

/* -------------------
   Summary Row (4 small cards)
   ------------------- */
@Composable
fun SummaryRow(usage: List<UsagePreview>) {
    val total = usage.sumOf { it.totalTimeInForeground }
    val top = usage.maxByOrNull { it.totalTimeInForeground }?.appName ?: "-"
    val peakHour = 18 // hardcoded for preview
    val launches = 42 // fake

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        SummaryCard(title = "Łącznie", value = formatDurationMs(total), modifier = Modifier.weight(1f))
        SummaryCard(title = "Najwięcej", value = top, modifier = Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        SummaryCard(title = "Szczyt", value = "${peakHour}:00", modifier = Modifier.weight(1f))
        SummaryCard(title = "Uruchomień", value = "$launches", modifier = Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.Center) {
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

/* -------------------
   HourTimeline - single-line 24 segments
   hours: List<Long> (size should be 24; if less, it's padded with zeros)
   ------------------- */
@Composable
fun HourTimeline(hours: List<Long>, height: Dp = 40.dp) {
    // pad to 24
    val full = (0..23).mapIndexed { idx, _ -> hours.getOrNull(idx) ?: 0L }
    val maxTime = max(1L, full.maxOrNull() ?: 1L)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        full.forEach { t ->
            HourSegment(
                fraction = if (maxTime <= 0L) 0f else t.toFloat() / maxTime.toFloat(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun HourSegment(fraction: Float, modifier: Modifier = Modifier) {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val fill = MaterialTheme.colorScheme.primary
    val alpha = (0.06f + 0.94f * fraction).coerceIn(0.06f, 0.95f)
    val targetColor = if (fraction <= 0f) base else fill.copy(alpha = alpha)
    val animated by animateColorAsState(targetValue = targetColor)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 0.7.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(animated)
    )
}

/* -------------------
   App list row with mini timeline
   ------------------- */
@Composable
fun AppListRow(appName: String, total: Long, hours: List<Long>) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(appName, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                // mini timeline: shrinked height
                HourTimeline(hours = hours, height = 26.dp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(formatDurationMs(total), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("visible", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/* -------------------
   Preview host (call this from Activity or Compose Preview)
   ------------------- */
@Composable
fun PreviewUsageDayHost() {
    MaterialTheme {
        UsageDayPreviewScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySelector(
    date: Date,
    onDateChange: (Date) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    val formatter = remember {
        java.text.SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                onDateChange(Date(date.time - 24 * 60 * 60 * 1000))
            }
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Poprzedni dzień")
        }

        Text(
            text = formatter.format(date),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { showPicker = true }
        )

        IconButton(
            onClick = {
                onDateChange(Date(date.time + 24 * 60 * 60 * 1000))
            }
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Następny dzień")
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("OK")
                }
            }
        ) {
            val state = rememberDatePickerState(
                initialSelectedDateMillis = date.time
            )
            DatePicker(
                state = state,
                showModeToggle = false
            )

            LaunchedEffect(state.selectedDateMillis) {
                state.selectedDateMillis?.let {
                    onDateChange(Date(it))
                }
            }
        }
    }
}
