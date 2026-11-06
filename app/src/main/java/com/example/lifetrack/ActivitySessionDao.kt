package com.example.lifetrack

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivitySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ActivitySessionEntity)

    @Query("SELECT * FROM activity_sessions ORDER BY startTime DESC")
    fun getAllSessionsFlow(): Flow<List<ActivitySessionEntity>>
}