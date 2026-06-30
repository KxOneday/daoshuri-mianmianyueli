package com.timecalendar.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.timecalendar.app.data.local.dao.CountdownEventDao
import com.timecalendar.app.data.local.dao.HabitRecordDao
import com.timecalendar.app.data.local.dao.PeriodRecordDao
import com.timecalendar.app.data.local.entity.CountdownEvent
import com.timecalendar.app.data.local.entity.HabitRecord
import com.timecalendar.app.data.local.entity.PeriodRecord

@Database(
    entities = [CountdownEvent::class, PeriodRecord::class, HabitRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countdownEventDao(): CountdownEventDao
    abstract fun periodRecordDao(): PeriodRecordDao
    abstract fun habitRecordDao(): HabitRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timecalendar_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
