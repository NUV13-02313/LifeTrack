package com.example.lifetrack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "today_todos")
data class TodayTodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val title: String,
    val priority: Int = 2,
    val isCompleted: Boolean = false
)