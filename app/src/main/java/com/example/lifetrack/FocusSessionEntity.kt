package com.example.lifetrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskTitle: String,
    val plannedMinutes: Int,
    val actualMinutes: Int,
    val startTime: Long,
    val endTime: Long,
    val note: String? = null,       // NEW: Optional note
    val status: String = "Completed" // NEW: Completed, Stopped Early, Paused
)