package com.example.lifetrack

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneUsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: PhoneUsageEntity)

    @Query("SELECT * FROM phone_usage WHERE date = :date ORDER BY usageMinutes DESC")
    fun getUsageForDate(date: String): Flow<List<PhoneUsageEntity>>

    @Query("DELETE FROM phone_usage WHERE date = :date")
    suspend fun deleteUsageForDate(date: String)
}