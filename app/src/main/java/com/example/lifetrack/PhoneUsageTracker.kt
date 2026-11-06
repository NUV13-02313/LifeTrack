package com.example.lifetrack

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object PhoneUsageTracker {

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }

        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageStatsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    suspend fun getTodayUsage(context: Context): List<PhoneUsageEntity> {
        if (!hasUsageStatsPermission(context)) return emptyList()

        return withContext(Dispatchers.IO) {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val startTime = calendar.timeInMillis
            val endTime = System.currentTimeMillis()

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            val packageManager = context.packageManager

            usageStatsList
                .filter { it.totalTimeInForeground > 60_000L }
                .groupBy { it.packageName }
                .map { (packageName, statsList) ->
                    val totalMs = statsList.sumOf { it.totalTimeInForeground }
                    val minutes = totalMs / 60_000L

                    val appName = try {
                        val appInfo = packageManager.getApplicationInfo(packageName, 0)
                        packageManager.getApplicationLabel(appInfo).toString()
                    } catch (e: Exception) {
                        packageName.substringAfterLast(".")
                            .replaceFirstChar { it.uppercase() }
                    }

                    PhoneUsageEntity(
                        appName = appName,
                        packageName = packageName,
                        usageMinutes = minutes,
                        date = today
                    )
                }
                .sortedByDescending { it.usageMinutes }
                .take(6)
        }
    }
}