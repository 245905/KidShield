package com.dominik.control.kidshield.data.core

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

class AppTimeProvider(context: Context) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private var allPackages: List<UsageStatsEntity> = emptyList()
    private var hourlyStats: List<HourlyStatsEntity> = emptyList()

    fun fetchUsageStats(userApps: Map<String, AppInfoEntity>): List<UsageStatsEntity>{
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 60 * 24

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        val filteredStats = stats.filter { stat ->
            stat.packageName in userApps.keys
        }

        val userStats = filteredStats.map { stat ->
            
            UsageStatsEntity(
                date = Date(stat.lastTimeStamp),
                appName = userApps[stat.packageName]!!.appName,
                packageName = stat.packageName,
                isSystemApp = userApps[stat.packageName]!!.isSystemApp,
                lastTimeUsed = stat.lastTimeUsed,
                totalTimeInForeground = stat.totalTimeInForeground,
                totalTimeVisible = stat.totalTimeVisible
            )
        }

        allPackages = userStats
        return userStats
    }

    fun fetchUsageEvents(interestedPackages: Set<String>? = null, lookBack: Long = 1000*60*60L): List<HourlyStatsEntity> {
        val hourlyBuckets = MutableList(24){0L}
        val hourlyBucketsPerPackage = mutableMapOf<String, LongArray>()

        fun addDurationToHourlyBuckets(start: Long, end: Long, packageName: String?) {
            var cur = start
            while (cur < end) {
                val zoned = Instant.ofEpochMilli(cur).atZone(ZoneId.systemDefault())
                val startOfNextHour = zoned.truncatedTo(ChronoUnit.HOURS).plusHours(1)
                val hourEndMs = minOf(end, startOfNextHour.toInstant().toEpochMilli())
                val hourIndex = zoned.hour // 0..23
                val delta = hourEndMs - cur

                hourlyBuckets[hourIndex] = hourlyBuckets[hourIndex] + delta

                if (packageName != null) {
                    val arr = hourlyBucketsPerPackage.getOrPut(packageName) { LongArray(24) { 0L } }
                    arr[hourIndex] = arr[hourIndex] + delta
                }

                cur = hourEndMs
            }
        }

        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 60 * 24

        var currentForegroundPkg: String? = null
        var currentForegroundStart: Long = -1L
        var screenUnlocked = true // needed for the emulator, possible better false in production

        // determining starting state
        val preStart = maxOf(0L, startTime - lookBack)
        val preEvents = usageStatsManager.queryEvents(preStart, startTime)
        val preEvent = UsageEvents.Event()
        while (preEvents.hasNextEvent()){
            preEvents.getNextEvent(preEvent)
            when(preEvent.eventType){
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    currentForegroundPkg = preEvent.packageName
                    currentForegroundStart = startTime
                }

                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    currentForegroundPkg = null
                    currentForegroundStart = -1L
                }

                UsageEvents.Event.KEYGUARD_HIDDEN -> {
                    screenUnlocked = true
                }

                UsageEvents.Event.KEYGUARD_SHOWN -> {
                    screenUnlocked = false
                }
            }
        }

        // main loop
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()
        while (events.hasNextEvent()){
            events.getNextEvent(event)
            val t = event.timeStamp
            when(event.eventType){
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    if(currentForegroundPkg != null && currentForegroundStart >= 0L){
                        if(screenUnlocked){
                            addDurationToHourlyBuckets(
                                currentForegroundStart,
                                minOf(t, endTime),
                                if(interestedPackages != null && currentForegroundPkg in interestedPackages)
                                    currentForegroundPkg else null
                            )
                        }
                    }
                    currentForegroundPkg = event.packageName
                    currentForegroundStart = t
                }

                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    if(currentForegroundPkg != null && currentForegroundStart>=0L){
                        if(screenUnlocked){
                            addDurationToHourlyBuckets(
                                currentForegroundStart,
                                minOf(t, endTime),
                                if(interestedPackages != null && currentForegroundPkg in interestedPackages)
                                    currentForegroundPkg else null
                            )
                        }
                    }
                    currentForegroundPkg = null
                    currentForegroundStart = -1L
                }

                UsageEvents.Event.KEYGUARD_HIDDEN -> {
                    screenUnlocked = true
                }

                UsageEvents.Event.KEYGUARD_SHOWN -> {
                    if(currentForegroundPkg != null && currentForegroundStart>=0L){
                        addDurationToHourlyBuckets(
                            currentForegroundStart,
                            minOf(t, endTime),
                            if(interestedPackages != null && currentForegroundPkg in interestedPackages)
                                currentForegroundPkg else null
                        )
                    }
                    currentForegroundPkg = null
                    currentForegroundStart = -1L
                    screenUnlocked = false
                }
            }
        }

        // if something is left
        if(currentForegroundPkg != null && currentForegroundStart>=0L){
            if(screenUnlocked){
                addDurationToHourlyBuckets(
                    currentForegroundStart,
                    endTime,
                    if(interestedPackages != null && currentForegroundPkg in interestedPackages)
                        currentForegroundPkg else null
                )
            }
        }

        val date = Date()
        val hourlyStats = hourlyBuckets.mapIndexed { hourIndex, totalMs ->
            HourlyStatsEntity(
                date = date,
                hour = hourIndex,
                totalTime = totalMs,
                packageName = null
            )
        }
        val hourlyStatsPerPackage = hourlyBucketsPerPackage.flatMap { (packageName, buckets) ->
            buckets.mapIndexed { hourIndex, totalMs ->
                HourlyStatsEntity(
                    date = date,
                    hour = hourIndex,
                    totalTime = totalMs,
                    packageName = packageName
                )
            }
        }

        val combinedStats = mutableListOf<HourlyStatsEntity>()
        combinedStats.addAll(hourlyStats)
        combinedStats.addAll(hourlyStatsPerPackage)

        this.hourlyStats = combinedStats
        return combinedStats
    }

}
