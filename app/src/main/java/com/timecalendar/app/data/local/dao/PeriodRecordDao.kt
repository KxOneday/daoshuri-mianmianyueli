package com.timecalendar.app.data.local.dao

import androidx.room.*
import com.timecalendar.app.data.local.entity.PeriodRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodRecordDao {

    @Query("SELECT * FROM period_records ORDER BY startDate DESC")
    fun getAll(): Flow<List<PeriodRecord>>

    @Query("SELECT * FROM period_records ORDER BY startDate DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<PeriodRecord>>

    @Query("SELECT * FROM period_records WHERE id = :id")
    suspend fun getById(id: Long): PeriodRecord?

    @Query("SELECT * FROM period_records WHERE startDate >= :startDate AND startDate <= :endDate ORDER BY startDate DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<PeriodRecord>>

    @Query("SELECT * FROM period_records WHERE startDate >= :date AND startDate <= :endDate")
    suspend fun getOverlapping(date: Long, endDate: Long): List<PeriodRecord>

    @Query("SELECT * FROM period_records ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatest(): PeriodRecord?

    @Query("SELECT * FROM period_records ORDER BY startDate DESC LIMIT :count")
    suspend fun getLastN(count: Int): List<PeriodRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PeriodRecord): Long

    @Update
    suspend fun update(record: PeriodRecord)

    @Delete
    suspend fun delete(record: PeriodRecord)

    @Query("DELETE FROM period_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM period_records")
    fun getCount(): Flow<Int>
}
