//package com.dominik.control.kidshield.ui.composable.screen
//
//import android.annotation.SuppressLint
//import androidx.compose.animation.core.TweenSpec
//import androidx.compose.animation.core.animateDpAsState
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CalendarToday
//import androidx.compose.material.icons.filled.KeyboardArrowLeft
//import androidx.compose.material.icons.filled.Timer
//import androidx.compose.material3.Card
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
//import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import kotlin.math.max
//
///* -------------------------
//   HELPERS
//   ------------------------- */
//
//fun formatDurationMs(ms: Long): String {
//    if (ms <= 0L) return "0s"
//    var seconds = ms / 1000
//    val hours = seconds / 3600
//    seconds -= hours * 3600
//    val minutes = seconds / 60
//    seconds -= minutes * 60
//    return buildString {
//        if (hours > 0) append("${hours}h ")
//        if (minutes > 0) append("${minutes}m ")
//        append("${seconds}s")
//    }
//}
//
//@SuppressLint("SimpleDateFormat")
//fun formatDateShort(date: Date): String {
//    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    return fmt.format(date)
//}
//
///* -------------------------
//   VIEWMODEL (przykładowy)
//   ------------------------- */
//
//class UsagePreviewViewModel : ViewModel() {
//
//    private val _usageList = MutableLiveData<List<UsageStatsEntity>>(fakeUsage())
//    val usageList: LiveData<List<UsageStatsEntity>> = _usageList
//
//    private val _hourlyList = MutableLiveData<List<HourlyStatsEntity>>(fakeHourly())
//    val hourlyList: LiveData<List<HourlyStatsEntity>> = _hourlyList
//
//    // zwróć hourly po packageName i dacie (upraszczając)
//    fun hourlyFor(packageName: String, date: Date): List<HourlyStatsEntity> {
//        return _hourlyList.value?.filter {
//            it.packageName == packageName && sameDay(it.date, date)
//        } ?: emptyList()
//    }
//
//    fun loadForDay(date: Date) {
//        // tutaj docelowo ładowanie z repo
//        // w preview nic nie robimy (mamy już dane)
//    }
//
//    private fun sameDay(a: Date, b: Date): Boolean {
//        val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//        return fmt.format(a) == fmt.format(b)
//    }
//
//    companion object {
//        // przykładowe dane
//        private fun fakeUsage(): List<UsageStatsEntity> {
//            val now = System.currentTimeMillis()
//            val today = Date(now)
//            return listOf(
//                UsageStatsEntity(
//                    id = 1,
//                    date = today,
//                    appName = "YouTube",
//                    packageName = "com.google.android.youtube",
//                    isSystemApp = false,
//                    lastTimeUsed = now - 1000L * 60 * 10,
//                    totalTimeInForeground = 1000L * 60 * 90,
//                    totalTimeVisible = 1000L * 60 * 80
//                ),
//                UsageStatsEntity(
//                    id = 2,
//                    date = today,
//                    appName = "Chrome",
//                    packageName = "com.android.chrome",
//                    isSystemApp = true,
//                    lastTimeUsed = now - 1000L * 60 * 30,
//                    totalTimeInForeground = 1000L * 60 * 40,
//                    totalTimeVisible = 1000L * 60 * 35
//                ),
//                UsageStatsEntity(
//                    id = 3,
//                    date = today,
//                    appName = "Spotify",
//                    packageName = "com.spotify.music",
//                    isSystemApp = false,
//                    lastTimeUsed = now - 1000L * 60 * 120,
//                    totalTimeInForeground = 1000L * 60 * 10,
//                    totalTimeVisible = 1000L * 60 * 9
//                )
//            )
//        }
//
//        private fun fakeHourly(): List<HourlyStatsEntity> {
//            val now = System.currentTimeMillis()
//            val today = Date(now)
//            // example for YouTube, Chrome, Spotify across hours 8..22
//            val hours = (8..22).toList()
//            val out = mutableListOf<HourlyStatsEntity>()
//            hours.forEach { h ->
//                out += HourlyStatsEntity(
//                    id = out.size + 1,
//                    date = today,
//                    hour = h,
//                    totalTime = if (h in 18..20) 1000L * 60 * 20 else 1000L * 60 * (h % 7),
//                    packageName = "com.google.android.youtube"
//                )
//            }
//            // Chrome smaller usage in morning
//            (8..22).forEach { h ->
//                out += HourlyStatsEntity(
//                    id = out.size + 1,
//                    date = today,
//                    hour = h,
//                    totalTime = if (h in 9..11) 1000L * 60 * 30 else 1000L * 60 * (h % 5),
//                    packageName = "com.android.chrome"
//                )
//            }
//            // Spotify minimal
//            (8..22).forEach { h ->
//                out += HourlyStatsEntity(
//                    id = out.size + 1,
//                    date = today,
//                    hour = h,
//                    totalTime = if (h in 7..9) 1000L * 60 * 15 else 1000L * 60 * (h % 3),
//                    packageName = "com.spotify.music"
//                )
//            }
//            return out
//        }
//    }
//}
//
///* -------------------------
//   SCREENS
//   ------------------------- */
//
///**
// * Overview screen showing date and top N apps by totalTimeInForeground.
// * onAppSelected -> open detail screen
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UsageOverviewScreen(
//    viewModel: UsagePreviewViewModel,
//    onBack: (() -> Unit)? = null,
//    onAppSelected: (UsageStatsEntity) -> Unit
//) {
//    val usage by viewModel.usageList.observeAsState(emptyList())
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    LaunchedEffect(Unit) { viewModel.loadForDay(Date()) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Użycie — ${formatDateShort(Date())}", color = MaterialTheme.colorScheme.onBackground) },
////                navigationIcon = if (onBack != null) {
////                    {
////                        IconButton(onClick = onBack) {
////                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back")
////                        }
////                    }
////                } else null
//            )
//        },
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        Column(Modifier
//            .fillMaxSize()
//            .padding(padding)
//            .padding(12.dp)
//        ) {
//
//            Text("Najdłużej używane", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // top 3
//            val top = usage.sortedByDescending { it.totalTimeInForeground }.take(5)
//            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                items(top) { stat ->
//                    UsageListItem(stat = stat, onClick = { onAppSelected(stat) })
//                }
//            }
//        }
//    }
//}
//
///**
// * List screen: shows all apps for selected day.
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UsageListScreen(
//    viewModel: UsagePreviewViewModel,
//    onAppSelected: (UsageStatsEntity) -> Unit,
//    onBack: () -> Unit
//) {
//    val list by viewModel.usageList.observeAsState(emptyList())
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Lista używanych aplikacji", color = MaterialTheme.colorScheme.onBackground) },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back")
//                    }
//                }
//            )
//        },
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(12.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(list) { stat ->
//                UsageListItem(stat = stat, onClick = { onAppSelected(stat) })
//            }
//        }
//    }
//}
//
///**
// * Small row item used both on overview and list.
// */
//@Composable
//fun UsageListItem(stat: UsageStatsEntity, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() },
//        shape = RoundedCornerShape(10.dp)
//    ) {
//        Row(modifier = Modifier
//            .padding(12.dp)
//            .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // left block with time
//            Column(modifier = Modifier.weight(1f)) {
//                Text(text = stat.appName, fontSize = 16.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
//                Spacer(modifier = Modifier.height(6.dp))
//                Text(text = stat.packageName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
//            }
//
//            // times
//            Column(horizontalAlignment = Alignment.End) {
//                Text(text = formatDurationMs(stat.totalTimeInForeground), fontWeight = FontWeight.Bold, fontSize = 14.sp)
//                Spacer(modifier = Modifier.height(4.dp))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(Icons.Default.Timer, contentDescription = "last", modifier = Modifier.size(15.dp))
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text(text = "last: ${formatDurationMs(max(0L, System.currentTimeMillis() - stat.lastTimeUsed))}", fontSize = 12.sp)
//                }
//            }
//        }
//    }
//}
//
///**
// * Detail screen: shows header and hourly bar chart.
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UsageDetailScreen(
//    viewModel: UsagePreviewViewModel,
//    stat: UsageStatsEntity,
//    onBack: () -> Unit
//) {
//    val hourly = remember { viewModel.hourlyFor(stat.packageName, stat.date) } // simple fetch
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(stat.appName, color = MaterialTheme.colorScheme.onBackground) },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back")
//                    }
//                }
//            )
//        },
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .padding(padding)
//            .padding(12.dp)
//        ) {
//            // header
//            Surface(shape = RoundedCornerShape(8.dp), tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
//                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(stat.appName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
//                        Text(stat.packageName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                    }
//                    Column(horizontalAlignment = Alignment.End) {
//                        Text(formatDurationMs(stat.totalTimeInForeground), fontWeight = FontWeight.Bold)
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text("Visible: ${formatDurationMs(stat.totalTimeVisible)}", fontSize = 12.sp)
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text("Wykres godzinowy", fontWeight = FontWeight.Bold)
//            Spacer(modifier = Modifier.height(8.dp))
//
//            HourlyBarChart(
//                hourly = hourly,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(220.dp)
//            )
//        }
//    }
//}
//
///* -------------------------
//   Hourly bar chart
//   ------------------------- */
//
///**
// * Simple hourly bar chart: receives list of HourlyStatsEntity (hour 0..23).
// * - Renders bars for present hours
// * - Click a bar to show tooltip (selected hour)
// * - Animates bar heights
// */
//@Composable
//fun HourlyBarChart(hourly: List<HourlyStatsEntity>, modifier: Modifier = Modifier) {
//    // Normalize data across provided hours
//    if (hourly.isEmpty()) {
//        Box(modifier = modifier, contentAlignment = Alignment.Center) {
//            Text("Brak danych godzinowych", color = MaterialTheme.colorScheme.onSurfaceVariant)
//        }
//        return
//    }
//
//    val maxTime = (hourly.maxOfOrNull { it.totalTime } ?: 1L).coerceAtLeast(1L)
//    var selectedHour by remember { mutableStateOf<Int?>(null) }
//
//    Row(modifier = modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//        hourly.forEach { h ->
//            val fraction = h.totalTime.toFloat() / maxTime.toFloat()
//            val targetHeight: Dp = (fraction * 180f).dp // 180dp max bar height
//            val animatedHeight by animateDpAsState(targetValue = targetHeight, animationSpec = TweenSpec(durationMillis = 350))
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxHeight()
//                    .clickable {
//                        selectedHour = if (selectedHour == h.hour) null else h.hour
//                    },
//                verticalArrangement = Arrangement.Bottom,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // value label if selected
//                if (selectedHour == h.hour) {
//                    Text(formatDurationMs(h.totalTime), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
//                    Spacer(modifier = Modifier.height(4.dp))
//                }
//
//                Box(
//                    modifier = Modifier
//                        .width(18.dp)
//                        .height(animatedHeight)
//                        .clip(RoundedCornerShape(4.dp))
//                        .background(MaterialTheme.colorScheme.primary)
//                )
//
//                Spacer(modifier = Modifier.height(6.dp))
//                Text(text = "${h.hour}:00", fontSize = 11.sp)
//            }
//        }
//    }
//}
