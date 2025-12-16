package com.dominik.control.kidshield.data.core

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity

class AppInfoProvider(context: Context) {
    private val packageManager: PackageManager = context.packageManager
    private var allPackages: List<AppInfoEntity> = emptyList()

    fun fetchInstalledApps(): List<AppInfoEntity> {
        val apps = packageManager.getInstalledApplications(0)
        val packages = apps.map { pkg ->
            val appInfo = packageManager.getPackageInfo(pkg.packageName, 0)
            val appName = packageManager.getApplicationLabel(pkg).toString()
            val isSystemApp = (pkg.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            AppInfoEntity(
                appName = appName,
                packageName = pkg.packageName,
                versionName = appInfo.versionName,
                versionCode = appInfo.longVersionCode,
                firstInstallTime = appInfo.firstInstallTime,
                lastUpdateTime = appInfo.lastUpdateTime,
                isSystemApp = isSystemApp,
                referenceNumber = null
            )
        }

        allPackages = packages;
        return packages;
    }

    fun getUserAppsNames(): Map<String, AppInfoEntity>{
        val npkgs = allPackages.filter { app ->
            !app.isSystemApp
        }
        return npkgs.associateBy { app ->
            app.packageName
        }
    }

    fun getAllAppsNames(): Map<String, AppInfoEntity> {
        return allPackages.associateBy { app ->
            app.packageName
        }
    }

}
