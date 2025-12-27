//package com.dominik.control.kidshield.ui.composable.screen
//
//import androidx.compose.animation.animateColorAsState
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
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.KeyboardArrowLeft
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExposedDropdownMenuBox
//import androidx.compose.material3.ExposedDropdownMenuDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.Surface
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.ShapeDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import kotlin.math.roundToInt
//
///* -------------------------
//   Helpery formatowania
//   ------------------------- */
//
//private fun formatDurationMsShort(ms: Long): String {
//    if (ms <= 0L) return "0s"
//    var seconds = ms / 1000
//    val hours = seconds / 3600
//    seconds -= hours * 3600
//    val minutes = seconds / 60
//    seconds -= minutes * 60
//    return buildString {
//        if (hours > 0) append("${hours}h ")
//        if (minutes > 0) append("${minutes}m ")
//        if (hours == 0L && minutes == 0L) append("${seconds}s")
//    }
//}
//
//@Suppress("SimpleDateFormat")
//private fun dateOnly(d: Date): String {
//    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    return fmt.format(d)
//}
//
///* -------------------------
//   Example ViewModel (fake data)
//   ------------------------- */
//
//class HourlyOverviewPreviewViewModel : ViewModel() {
//    // All hourly entries (includes entries with packageName == null = phone-wide)
//    private val _hourly = MutableLiveData<List<HourlyStatsEntity>>(fakeHourly())
//    val hourly: LiveData<List<HourlyStatsEntity>> = _hourly
//
//    // zwraca distinct package names (null excluded) w kolejności
//    fun packagesForDay(date: Date): List<String> {
//        val list = _hourly.value ?: emptyList()
//        return list.filter { sameDay(it.date, date) && it.packageName != null }
//            .mapNotNull { it.packageName }
//            .distinct()
//            .sorted()
//    }
//
//    fun hourlyFor(packageName: String?, date: Date): List<HourlyStatsEntity> {
//        val list = _hourly.value ?: emptyList()
//        return list.filter { sameDay(it.date, date) && it.packageName == packageName }
//            .sortedBy { it.hour }
//    }
//
//    private fun sameDay(a: Date, b: Date): Boolean {
//        val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//        return fmt.format(a) == fmt.format(b)
//    }
//
//    companion object {
//        // prosty generator danych testowych (zawiera również wiersze z packageName = null)
//        private fun fakeHourly(): List<HourlyStatsEntity> {
//            val now = System.currentTimeMillis()
//            val today = Date(now)
//            val out = mutableListOf<HourlyStatsEntity>()
//
//            // phone-level aggregated usage (packageName = null) - show some usage in evening
//            (0..23).forEach { h ->
//                val base = when {
//                    h in 7..9 -> 10L * 60 * 1000L
//                    h in 18..21 -> 45L * 60 * 1000L
//                    else -> (h % 5) * 5L * 60 * 1000L
//                }
//                out += HourlyStatsEntity(id = out.size + 1, date = today, hour = h, totalTime = base, packageName = null)
//            }
//
//            // YouTube heavy in evening
//            (0..23).forEach { h ->
//                val t = if (h in 18..20) 25L * 60 * 1000L else (h % 6) * 2L * 60 * 1000L
//                out += HourlyStatsEntity(id = out.size + 1, date = today, hour = h, totalTime = t, packageName = "com.google.android.youtube")
//            }
//
//            // Chrome moderate in morning
//            (0..23).forEach { h ->
//                val t = if (h in 8..11) 15L * 60 * 1000L else (h % 4) * 3L * 60 * 1000L
//                out += HourlyStatsEntity(id = out.size + 1, date = today, hour = h, totalTime = t, packageName = "com.android.chrome")
//            }
//
//            // Spotify small early morning
//            (0..23).forEach { h ->
//                val t = if (h in 6..9) 10L * 60 * 1000L else (h % 3) * 60 * 1000L
//                out += HourlyStatsEntity(id = out.size + 1, date = today, hour = h, totalTime = t, packageName = "com.spotify.music")
//            }
//
//            return out
//        }
//    }
//}
//
///* -------------------------
//   SCREEN: Hourly Overview (heatmap-like)
//   ------------------------- */
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HourlyOverviewScreen(
//    viewModel: HourlyOverviewPreviewViewModel,
//    date: Date = Date(),
//    onBack: (() -> Unit)? = null
//) {
//    val allHourly by viewModel.hourly.observeAsState(emptyList())
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    // build app list for dropdown: "Cały telefon" (null) + package names
//    val packages = remember(date, allHourly) {
//        mutableStateListOf<String>().apply {
//            add("=== Cały telefon ===") // marker for null (phone aggregated)
//            addAll(viewModel.packagesForDay(date))
//        }
//    }
//
//    var selectedPackageLabel by remember { mutableStateOf(packages.firstOrNull() ?: "=== Cały telefon ===") }
//    // map label to packageName: label "=== Cały telefon ===" -> null
//    val selectedPackageName: String? = if (selectedPackageLabel == "=== Cały telefon ===") null else selectedPackageLabel
//
//    LaunchedEffect(Unit) {
//        // optional: viewModel.refreshFor(date)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Godzinowe użycie — ${dateOnly(date)}", color = MaterialTheme.colorScheme.onBackground) },
////                navigationIcon = if (onBack != null) {
////                    { IconButton(onClick = onBack) { Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Powrót") } }
////                } else null
//            )
//        },
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(12.dp)
//        ) {
//            // Dropdown wyboru aplikacji
//            AppSelectorDropdown(
//                packages = packages,
//                selected = selectedPackageLabel,
//                onSelect = { selectedPackageLabel = it }
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // pobieramy hourly dla wybranego package (null -> phone-wide)
//            val hourlyForSelection = remember(selectedPackageName, allHourly, date) {
//                viewModel.hourlyFor(selectedPackageName, date)
//            }
//
//            // suma czasu
//            val total = hourlyForSelection.sumOf { it.totalTime }
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(text = "Suma: ${formatDurationMsShort(total)}", fontSize = 14.sp)
//                Text(
//                    text = if (selectedPackageName == null) "Wyświetlane: Cały telefon" else "Wyświetlane: ${selectedPackageLabel}",
//                    fontSize = 13.sp,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // LEGEND
//            LegendRow()
//
//            Spacer(modifier = Modifier.height(10.dp))
//
//            // GRID 6x4 (24 komórki)
//            HourlyHeatmapGrid(hourly = hourlyForSelection)
//        }
//    }
//}
//
///* -------------------------
//   Dropdown
//   ------------------------- */
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppSelectorDropdown(
//    packages: List<String>,
//    selected: String,
//    onSelect: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    ExposedDropdownMenuBox(
//        expanded = expanded,
//        onExpandedChange = { expanded = !expanded }
//    ) {
//        OutlinedTextField(
//            modifier = Modifier
//                .menuAnchor()
//                .fillMaxWidth(),
//            readOnly = true,
//            value = selected,
//            onValueChange = {},
//            label = { Text("Wybierz aplikację") },
//            trailingIcon = {
//                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//            }
//        )
//
//        ExposedDropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            packages.forEach { label ->
//                DropdownMenuItem(
//                    text = { Text(label) },
//                    onClick = {
//                        onSelect(label)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}
//
///* -------------------------
//   Legend
//   ------------------------- */
//
//@Composable
//fun LegendRow() {
//    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
//        val base = MaterialTheme.colorScheme.primary
//        // three sample boxes: low / mid / high
//        Box(modifier = Modifier.size(18.dp).clip(RoundedCornerShape(4.dp)).background(base.copy(alpha = 0.15f)))
//        Spacer(modifier = Modifier.width(8.dp))
//        Text("mało", fontSize = 12.sp)
//        Spacer(modifier = Modifier.width(12.dp))
//        Box(modifier = Modifier.size(18.dp).clip(RoundedCornerShape(4.dp)).background(base.copy(alpha = 0.45f)))
//        Spacer(modifier = Modifier.width(8.dp))
//        Text("średnio", fontSize = 12.sp)
//        Spacer(modifier = Modifier.width(12.dp))
//        Box(modifier = Modifier.size(18.dp).clip(RoundedCornerShape(4.dp)).background(base.copy(alpha = 0.9f)))
//        Spacer(modifier = Modifier.width(8.dp))
//        Text("intensywnie", fontSize = 12.sp)
//    }
//}
//
///* -------------------------
//   GRID + CELL
//   ------------------------- */
//
//@Composable
//fun HourlyHeatmapGrid(hourly: List<HourlyStatsEntity>) {
//    // ensure we have 24 items (if missing, fill zeroes)
//    val map = hourly.associateBy { it.hour }
//    val full = (0..23).map { h -> map[h] ?: HourlyStatsEntity(id = -1, date = Date(), hour = h, totalTime = 0L, packageName = hourly.firstOrNull()?.packageName) }
//
//    val maxTime = (full.maxOfOrNull { it.totalTime } ?: 1L).coerceAtLeast(1L)
//
////    LazyVerticalGrid(
////        columns = GridCells.Fixed(6),
////        modifier = Modifier
////            .fillMaxWidth()
////            .height(280.dp),
////        verticalArrangement = Arrangement.spacedBy(8.dp),
////        horizontalArrangement = Arrangement.spacedBy(8.dp),
////        content = {
////            items(full) { h ->
////                HourCell(hour = h.hour, totalTime = h.totalTime, maxTime = maxTime)
////            }
////        }
////    )
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(32.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .background(MaterialTheme.colorScheme.surfaceVariant)
//    ) {
//        map.forEach { hour ->
//            HourSegment(
//                totalTime = hour.value.totalTime,
//                maxTime = maxTime,
//                modifier = Modifier.weight(1f)
//            )
//        }
//    }
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text("0", fontSize = 12.sp)
//        Text("6")
//        Text("12")
//        Text("18")
//        Text("24")
//    }
//}
//
//@Composable
//fun HourSegment(
//    totalTime: Long,
//    maxTime: Long,
//    modifier: Modifier = Modifier
//) {
//    val fraction = if (maxTime == 0L) 0f else totalTime.toFloat() / maxTime
//    val fillAlpha = (0.05f + 0.95f * fraction).coerceIn(0.05f, 1f)
//
//    Box(
//        modifier = modifier
//            .fillMaxHeight()
//            .padding(horizontal = 0.5.dp)
//            .background(
//                MaterialTheme.colorScheme.primary.copy(alpha = fillAlpha),
//                RoundedCornerShape(2.dp)
//            )
//    )
//}
//
//@Composable
//fun HourCell(hour: Int, totalTime: Long, maxTime: Long) {
//    val fraction = if (maxTime <= 0L) 0f else totalTime.toFloat() / maxTime.toFloat()
//
//    val fillColor = MaterialTheme.colorScheme.primary
//    val targetColor = if (fraction <= 0f)
//        MaterialTheme.colorScheme.surfaceVariant
//    else
//        fillColor.copy(alpha = (0.1f + 0.9f * fraction).coerceIn(0.06f, 0.95f))
//
//    val animatedColor by animateColorAsState(targetValue = targetColor)
//
//    Surface(
//        shape = RoundedCornerShape(8.dp),
//        color = animatedColor,
//        modifier = Modifier.height(56.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(6.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text("$hour:00", fontSize = 12.sp)
//            Spacer(Modifier.height(4.dp))
//            Text(
//                text = if (totalTime > 0L) formatDurationMsShort(totalTime) else "-",
//                fontSize = 12.sp
//            )
//        }
//    }
//}
