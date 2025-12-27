package com.dominik.control.kidshield

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.dominik.control.kidshield.data.core.AppInfoProvider
import com.dominik.control.kidshield.data.core.AppTimeProvider
import com.dominik.control.kidshield.data.model.domain.AppInfoDiffEntity
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import com.dominik.control.kidshield.data.repository.AppInfoDiffRepository
import com.dominik.control.kidshield.data.repository.AppInfoRepository
import com.dominik.control.kidshield.data.repository.AuthManager
import com.dominik.control.kidshield.data.repository.TestRepository
import com.dominik.control.kidshield.ui.composable.navigation.NavigationStack
import com.dominik.control.kidshield.ui.composable.screen.DataScreen
//import com.dominik.control.kidshield.ui.composable.screen.HourlyOverviewPreviewViewModel
//import com.dominik.control.kidshield.ui.composable.screen.HourlyOverviewScreen
import com.dominik.control.kidshield.ui.composable.screen.LoginScreen
import com.dominik.control.kidshield.ui.composable.screen.PreviewUsageDayHost
//import com.dominik.control.kidshield.ui.composable.screen.UsageDetailScreen
//import com.dominik.control.kidshield.ui.composable.screen.UsageListScreen
//import com.dominik.control.kidshield.ui.composable.screen.UsageOverviewScreen
//import com.dominik.control.kidshield.ui.composable.screen.UsagePreviewViewModel
import com.dominik.control.kidshield.ui.controller.DataViewModel
import com.dominik.control.kidshield.ui.controller.LoginViewModel
import com.dominik.control.kidshield.ui.theme.KidShieldTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.util.Date

@HiltAndroidApp
class KidShield : Application()

@HiltViewModel
class UserViewModel @Inject constructor(
    private val appInfoRepository: AppInfoRepository,
    private val testRepository: TestRepository,
    private val appInfoDiffRepository: AppInfoDiffRepository
) : ViewModel() {

    suspend fun getAllAppInfo(): List<AppInfoEntity> {
        return appInfoRepository.getAllAppInfos()
    }

    suspend fun insertAppInfos(data: List<AppInfoEntity>): List<Long> {
        return appInfoRepository.insertAppInfos(data)
    }

    suspend fun deleteAppInfos(data: List<AppInfoEntity>): Int {
        return appInfoRepository.deleteAppInfos(data)
    }

    suspend fun callOpen(): Result<Unit> {
        return testRepository.open()
    }

    suspend fun callClosed(): Result<Unit> {
        return testRepository.closed()
    }

    suspend fun callRestricted(): Result<Unit> {
        return testRepository.restricted()
    }

    suspend fun pushData(data: List<AppInfoEntity>): Result<Unit> {
        return appInfoRepository.uploadData(data)
    }

    suspend fun pullData(): Result<List<AppInfoDiffEntity>> {
        return appInfoDiffRepository.downloadData()
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels() // hilt-provided
    private val dataViewModel: DataViewModel by viewModels() // hilt-provided

    @Inject lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))


        val mp = AppInfoProvider(this)
        mp.fetchInstalledApps()
        val userApps = mp.getUserAppsNames()

        val atp = AppTimeProvider(this)
        val s = atp.fetchUsageStats(userApps)
        for (si in s){
            Log.d("dev", si.toString())
        }

        val b = atp.fetchUsageEvents(userApps.keys)
        Log.d("dev", b.toString())

        for (bi in b){
            Log.d("dev", bi.toString())
        }

        val pkgs = mp.fetchInstalledApps()
        val npkgs = pkgs.filter { app ->
            !app.isSystemApp
        }
        Log.d("dev", npkgs.toString())
        for (app in npkgs){
            Log.d("dev", app.toString())
        }

        val userViewModel: UserViewModel by viewModels()
        lifecycleScope.launch {
//            val res = userViewModel.insertAppInfos(pkgs)
//            Log.d("dev-db", "insert $res")
            val res1 = userViewModel.deleteAppInfos(pkgs)
            Log.d("dev-db", "delete $res1")
            val res2 = userViewModel.getAllAppInfo()
            Log.d("dev-db", "get $res2")
            val res3 = userViewModel.getAllAppInfo()
            Log.d("dev-db", "get $res3")

            val res4 = userViewModel.callOpen()
            Log.d("dev-rm", "open $res4")
            val res5 = userViewModel.callClosed()
            Log.d("dev-rm", "closed $res5")
            val res6 = userViewModel.callRestricted()
            Log.d("dev-rm", "restricted $res6")
        }

//        Log.d("dev", "start")
////        val AppOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
//        val usm = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        Log.d("dev", (usm==null).toString())
//        val endTime = System.currentTimeMillis()
//        val startTime = endTime - 1000 * 60 * 60 * 24
//
//        val stats = usm.queryUsageStats(
//            UsageStatsManager.INTERVAL_DAILY,
//            startTime,
//            endTime
//        )
//        val filteredStats = stats.filter { stat ->
//            stat.packageName in userApps.keys
//        }
//        Log.d("dev", filteredStats.isNullOrEmpty().toString())
//        for (s in filteredStats){
//            Log.d("dev", s.packageName.toString())
//            Log.d("dev", s.firstTimeStamp.toString())
//            Log.d("dev", s.lastTimeForegroundServiceUsed.toString())
//            Log.d("dev", s.lastTimeStamp.toString())
//            Log.d("dev", s.lastTimeUsed.toString())
//            Log.d("dev", s.lastTimeVisible.toString())
//            Log.d("dev", s.totalTimeForegroundServiceUsed.toString())
//            Log.d("dev", s.totalTimeInForeground.toString())
//            Log.d("dev", s.totalTimeVisible.toString())
//
//        }
//
//
//        val usageEvents = usm.queryEvents(startTime, endTime)
//        val usageEvent = UsageEvents.Event()
//        while ( usageEvents.hasNextEvent() ) {
//            usageEvents.getNextEvent( usageEvent )
//            Log.e( "APP" , "${usageEvent.packageName} ${usageEvent.timeStamp} ${usageEvent.eventType}" )
//        }
//        Log.d("dev", "end")

//        val appInfoProvider = AppInfoProvider(this)
//        val pkgs = appInfoProvider.getInstalledApps()
//        val npkgs = pkgs.filter { app ->
//            !app.isSystemApp
//        }
//        Log.d("dev", npkgs.toString())
//        for (app in npkgs){
//            Log.d("dev", app.toString())
//        }
//
//
//        val pm = this.packageManager
//        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
//        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
////        for (appInfo in apps){
////            val appName = pm.getApplicationLabel(appInfo).toString()
////            Log.d("dev", appName)
////            Log.d("dev", appInfo.toString())
////
////        }
//        val appName = pm.getApplicationLabel(apps[0]).toString()
//        Log.d("dev", apps.size.toString())
//        Log.d("dev", appName)
//        Log.d("dev", apps[0].toString())
//        Log.d("dev", packages[0].toString())
//
//        val packageName = apps[0].packageName
//        val packageInfo = pm.getPackageInfo(packageName, 0)
//        val versionName = packageInfo.versionName
//        val versionCode = packageInfo.longVersionCode
//        val isSystemApp = (apps[0].flags and ApplicationInfo.FLAG_SYSTEM) != 0
//        Log.d("dev", packageName)
//        Log.d("dev", packageInfo.toString())
//        Log.d("dev", versionName.toString())
//        Log.d("dev", versionCode.toString())
//        Log.d("dev", isSystemApp.toString())
//        Log.d("dev", packageInfo.firstInstallTime.toString())
//        Log.d("dev", packageInfo.lastUpdateTime.toString())
//
////        for (appInfo in packages){
////            Log.d("dev", appInfo.toString())
////        }
//
//        val userApps = apps.filter { app ->
//            1 != null && // da się uruchomić z Launchera
//                    (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0          // nie jest systemowa
//        }
//        Log.d("dev", userApps.size.toString())
//        for (appInfo in userApps){
//            val apppName = pm.getApplicationLabel(appInfo).toString()
//            Log.d("dev", apppName)
//
//        }

        enableEdgeToEdge()
        setContent {
//            KidShieldTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//            LoginScreen(viewModel = viewModel, onNavigateToHome = {})
//            DataScreen(viewModel = dataViewModel, onNavigateToHome = {})

            KidShieldTheme {
//                NavigationStack(authManager)
//                PreviewUsageScreens()
//                PreviewHourly()
                PreviewUsageDayHost()
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KidShieldTheme {
        Greeting("Android")
    }
}

//@Composable
//fun PreviewUsageScreens() {
//    val vm = remember { UsagePreviewViewModel() }
//    var screen by remember { mutableStateOf("overview") }
//    var selected: UsageStatsEntity? by remember { mutableStateOf(null) }
//
//    when (screen) {
//        "overview" -> UsageOverviewScreen(vm, onAppSelected = {
//            selected = it; screen = "detail"
//        })
//        "list" -> UsageListScreen(vm, onAppSelected = {
//            selected = it; screen = "detail"
//        }, onBack = { screen = "overview" })
//        "detail" -> selected?.let {
//            UsageDetailScreen(vm, stat = it, onBack = { screen = "overview" })
//        }
//    }
//}
//
//@Composable
//fun PreviewHourly() {
//    val vm = remember { HourlyOverviewPreviewViewModel() }
//    HourlyOverviewScreen(viewModel = vm, date = Date(), onBack = null)
//}