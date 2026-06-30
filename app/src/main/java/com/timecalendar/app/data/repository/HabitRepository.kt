package com.timecalendar.app.data.repository

import com.timecalendar.app.data.local.dao.HabitRecordDao
import com.timecalendar.app.data.local.entity.HabitRecord
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val dao: HabitRecordDao) {

    fun getByDate(date: Long): Flow<List<HabitRecord>> = dao.getByDate(date)
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<HabitRecord>> = dao.getByDateRange(startDate, endDate)
    fun getByName(name: String): Flow<List<HabitRecord>> = dao.getByName(name)
    fun getCompletedByDate(date: Long): Flow<List<HabitRecord>> = dao.getCompletedByDate(date)
    fun getStreakCount(name: String, startDate: Long): Flow<Int> = dao.getStreakCount(name, startDate)

    suspend fun insert(record: HabitRecord): Long = dao.insert(record)
    suspend fun update(record: HabitRecord) = dao.update(record)
    suspend fun delete(record: HabitRecord) = dao.delete(record)
    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
