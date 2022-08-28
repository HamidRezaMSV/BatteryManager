package com.sm.batterymanager.utils

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import java.util.*

class BatteryUsage(private val context: Context) {

    init {
        // check permission
        if(getUsageStateList().isEmpty()){
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent)
        }
    }

    fun getUsageStateList() : List<UsageStats>{
        val usm = getUsageStateManager(context)
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (24 * 3600 * 1000)
        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY , startTime , endTime)
    }

    private fun getUsageStateManager(context: Context) : UsageStatsManager{
        return context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    fun getTotalTime() : Long{
        var totalTime : Long = 0
        for (item in getUsageStateList()){
            totalTime += item.totalTimeInForeground
        }
        return totalTime
    }
}