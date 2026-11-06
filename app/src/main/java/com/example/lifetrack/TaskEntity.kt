package com.example.lifetrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val priority: Int = 1,         // 1: Low, 2: Medium, 3: High
    val deadline: Long? = null,
    val status: String = "Not started"
)