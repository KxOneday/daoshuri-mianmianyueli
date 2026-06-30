package com.timecalendar.app.data.local.dao

import androidx.room.*
import com.timecalendar.app.data.local.entity.HabitRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitRecordDao {

    @Query("SELECT * FROM habit_records WHERE date = :date ORDER BY category, habitName")
    fun getByDate(date: Long): Flow<List<HabitRecord>>

    @Query("SELECT * FROM habit_records WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<HabitRecord>>

    @Query("SELECT * FROM habit_records WHERE habitName = :name ORDER BY date DESC")
    fun getByName(name: String): Flow<List<HabitRecord>>

    @Query("SELECT * FROM habit_records WHERE date = :date AND isCompleted = 1")
    fun getCompletedByDate(date: Long): Flow<List<HabitRecord>>

    @Query("SELECT COUNT(*) FROM habit_records WHERE habitName = :name AND isCompleted = 1 AND date >= :startDate")
    fun getStreakCount(name: String, startDate: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: HabitRecord): Long

    @Update
    suspend fun update(record: HabitRecord)

    @Delete
    suspend fun delete(record: HabitRecord)

    @Query("DELETE FROM habit_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
