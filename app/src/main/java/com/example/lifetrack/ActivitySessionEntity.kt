package com.example.lifetrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_sessions")
data class ActivitySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,       // e.g., "Study", "Exercise", "Sleep", "Phone"
    val startTime: Long,        // Timestamp when it started
    val endTime: Long,          // Timestamp when it ended
    val durationMinutes: Int    // Total duration in minutes
)