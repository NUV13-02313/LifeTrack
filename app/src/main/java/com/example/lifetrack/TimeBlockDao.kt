package com.example.lifetrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeBlockDao {
    @Insert suspend fun insert(b: TimeBlockEntity)
    @Update suspend fun update(b: TimeBlockEntity) // NEW
    @Delete suspend fun delete(b: TimeBlockEntity)
    @Query("SELECT * FROM time_blocks WHERE date = :date ORDER BY startMinutes ASC")
    fun observe(date: String): Flow<List<TimeBlockEntity>>
}