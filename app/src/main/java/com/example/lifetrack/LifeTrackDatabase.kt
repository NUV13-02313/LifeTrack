package com.example.lifetrack

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskEntity::class, ActivitySessionEntity::class,
        DailyReviewEntity::class, FocusSessionEntity::class, PhoneUsageEntity::class,
        TodayTodoEntity::class, TimeBlockEntity::class, DailyReportEntity::class // NEW
    ],
    version = 10, // <--- MUST BE 10
    exportSchema = false
)
abstract class LifeTrackDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun activitySessionDao(): ActivitySessionDao
    abstract fun dailyReviewDao(): DailyReviewDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun phoneUsageDao(): PhoneUsageDao
    abstract fun todayTodoDao(): TodayTodoDao
    abstract fun timeBlockDao(): TimeBlockDao
    abstract fun dailyReportDao(): DailyReportDao // NEW

    companion object {
        @Volatile
        private var INSTANCE: LifeTrackDatabase? = null

        fun getDatabase(context: Context): LifeTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeTrackDatabase::class.java,
                    "lifetrack_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}