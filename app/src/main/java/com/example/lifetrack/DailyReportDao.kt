package com.example.lifetrack

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: DailyReportEntity)

    @Query("SELECT * FROM daily_reports ORDER BY date DESC")
    fun getAllReports(): Flow<List<DailyReportEntity>>

    @Query("SELECT * FROM daily_reports WHERE date = :date")
    suspend fun getReportForDate(date: String): DailyReportEntity?

    @Query("DELETE FROM daily_reports WHERE date < :date")
    suspend fun deleteOlderThan(date: String)
}