package com.example.lifetrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodayTodoDao {
    @Insert suspend fun insert(t: TodayTodoEntity)
    @Update suspend fun update(t: TodayTodoEntity)
    @Delete suspend fun delete(t: TodayTodoEntity)
    @Query("SELECT * FROM today_todos WHERE date = :date ORDER BY priority DESC, id ASC")
    fun observe(date: String): Flow<List<TodayTodoEntity>>
}