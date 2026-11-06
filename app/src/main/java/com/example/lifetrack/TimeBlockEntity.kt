package com.example.lifetrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_blocks")
data class TimeBlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val startMinutes: Int,
    val endMinutes: Int,
    val title: String,
    val isCompleted: Boolean = false // NEW: Track completion status
)