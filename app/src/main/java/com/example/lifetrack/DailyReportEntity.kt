package com.example.lifetrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reports")
data class DailyReportEntity(
    @PrimaryKey val date: String, // e.g., "2026-11-06"
    val focusMins: Int = 0,
    val activityMins: Int = 0,
    val phoneMins: Int = 0,
    val tasksCompleted: Int = 0,
    val tasksTotal: Int = 0,
    val mood: Int? = null
)