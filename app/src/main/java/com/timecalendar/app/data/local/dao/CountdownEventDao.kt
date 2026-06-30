package com.timecalendar.app.data.local.dao

import androidx.room.*
import com.timecalendar.app.data.local.entity.CountdownEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CountdownEventDao {

    @Query("SELECT * FROM countdown_events WHERE isHidden = 0 ORDER BY isPinned DESC, targetDate ASC")
    fun getAllVisible(): Flow<List<CountdownEvent>>

    @Query("SELECT * FROM countdown_events ORDER BY isPinned DESC, targetDate ASC")
    fun getAll(): Flow<List<CountdownEvent>>

    @Query("SELECT * FROM countdown_events WHERE id = :id")
    suspend fun getById(id: Long): CountdownEvent?

    @Query("SELECT * FROM countdown_events WHERE category = :category AND isHidden = 0 ORDER BY targetDate ASC")
    fun getByCategory(category: String): Flow<List<CountdownEvent>>

    @Query("SELECT * FROM countdown_events WHERE targetDate >= :startDate AND targetDate <= :endDate AND isHidden = 0")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<CountdownEvent>>

    @Query("SELECT * FROM countdown_events WHERE isPinned = 1 AND isHidden = 0 ORDER BY targetDate ASC LIMIT :limit")
    fun getPinned(limit: Int = 5): Flow<List<CountdownEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CountdownEvent): Long

    @Update
    suspend fun update(event: CountdownEvent)

    @Delete
    suspend fun delete(event: CountdownEvent)

    @Query("DELETE FROM countdown_events WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT DISTINCT category FROM countdown_events")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM countdown_events")
    fun getCount(): Flow<Int>
}
