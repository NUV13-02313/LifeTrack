package com.example.lifetrack
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reviews")
data class DailyReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val mood: Int, // 1 to 5
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)