package com.timecalendar.app.data.repository

import com.timecalendar.app.data.local.dao.CountdownEventDao
import com.timecalendar.app.data.local.entity.CountdownEvent
import kotlinx.coroutines.flow.Flow

class CountdownRepository(private val dao: CountdownEventDao) {

    fun getAllVisible(): Flow<List<CountdownEvent>> = dao.getAllVisible()
    fun getAll(): Flow<List<CountdownEvent>> = dao.getAll()
    fun getByCategory(category: String): Flow<List<CountdownEvent>> = dao.getByCategory(category)
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<CountdownEvent>> = dao.getByDateRange(startDate, endDate)
    fun getPinned(limit: Int = 5): Flow<List<CountdownEvent>> = dao.getPinned(limit)
    fun getAllCategories(): Flow<List<String>> = dao.getAllCategories()
    fun getCount(): Flow<Int> = dao.getCount()

    suspend fun getById(id: Long): CountdownEvent? = dao.getById(id)
    suspend fun insert(event: CountdownEvent): Long = dao.insert(event)
    suspend fun update(event: CountdownEvent) = dao.update(event)
    suspend fun delete(event: CountdownEvent) = dao.delete(event)
    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
