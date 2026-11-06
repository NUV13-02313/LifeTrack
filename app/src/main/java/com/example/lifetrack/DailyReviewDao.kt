package com.example.lifetrack
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReviewDao {
    @Insert suspend fun insertReview(review: DailyReviewEntity)
    @Query("SELECT * FROM daily_reviews WHERE date = :date")
    fun getReviewForDate(date: String): Flow<DailyReviewEntity?>
}