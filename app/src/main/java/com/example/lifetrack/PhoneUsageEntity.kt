package com.example.lifetrack
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phone_usage")
data class PhoneUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appName: String,
    val packageName: String,
    val usageMinutes: Long,
    val date: String
)