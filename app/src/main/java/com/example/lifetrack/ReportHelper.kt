package com.example.lifetrack

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ReportHelper {

    // Gets yesterday's date in "yyyy-MM-dd" format
    fun getYesterdayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    // Calculates the 24-hour breakdown for a specific date
    suspend fun generateDailyReport(
        date: String,
        focusSessions: List<FocusSessionEntity>,
        activitySessions: List<ActivitySessionEntity>,
        phoneUsages: List<PhoneUsageEntity>,
        tasks: List<TaskEntity>
    ): DailyReportEntity {
        val focusMins = focusSessions.filter {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.startTime)) == date
        }.sumOf { it.actualMinutes }

        val activityMins = activitySessions.filter {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.startTime)) == date
        }.sumOf { it.durationMinutes }

        // FIXED: Added .toInt() because usageMinutes is Long but DailyReportEntity expects Int
        val phoneMins = phoneUsages.filter { it.date == date }.sumOf { it.usageMinutes }.toInt()

        val dayTasks = tasks

        return DailyReportEntity(
            date = date,
            focusMins = focusMins,
            activityMins = activityMins,
            phoneMins = phoneMins,
            tasksCompleted = dayTasks.count { it.status == "Completed" },
            tasksTotal = dayTasks.size
        )
    }
}