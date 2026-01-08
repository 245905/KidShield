package com.dominik.control.kidshield.utils

import android.app.ActivityManager
import android.content.Context

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return am.getRunningServices(Int.MAX_VALUE)
        .any { it.service.className == serviceClass.name }
}
