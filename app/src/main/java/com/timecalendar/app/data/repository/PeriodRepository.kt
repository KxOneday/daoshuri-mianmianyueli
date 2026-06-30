package com.timecalendar.app.data.repository

import com.timecalendar.app.data.local.dao.PeriodRecordDao
import com.timecalendar.app.data.local.entity.PeriodRecord
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class PeriodRepository(private val dao: PeriodRecordDao) {

    fun getAll(): Flow<List<PeriodRecord>> = dao.getAll()
    fun getRecent(limit: Int): Flow<List<PeriodRecord>> = dao.getRecent(limit)
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<PeriodRecord>> = dao.getByDateRange(startDate, endDate)
    fun getCount(): Flow<Int> = dao.getCount()

    suspend fun getById(id: Long): PeriodRecord? = dao.getById(id)
    suspend fun getLatest(): PeriodRecord? = dao.getLatest()
    suspend fun getLastN(count: Int): List<PeriodRecord> = dao.getLastN(count)
    suspend fun insert(record: PeriodRecord): Long = dao.insert(record)
    suspend fun update(record: PeriodRecord) = dao.update(record)
    suspend fun delete(record: PeriodRecord) = dao.delete(record)
    suspend fun deleteById(id: Long) = dao.deleteById(id)

    // Calculate average cycle length from last N records
    suspend fun getAverageCycleLength(): Int? {
        val records = dao.getLastN(6)
        if (records.size < 2) return null

        val cycles = mutableListOf<Int>()
        for (i in 0 until records.size - 1) {
            val diff = ((records[i].startDate - records[i + 1].startDate) / (1000 * 60 * 60 * 24)).toInt()
            if (diff in 15..45) cycles.add(diff)
        }
        return if (cycles.isNotEmpty()) cycles.average().toInt() else null
    }

    // Calculate average period duration
    suspend fun getAveragePeriodDuration(): Int? {
        val records = dao.getLastN(6)
        val durations = records.filter { it.endDate != null }.map {
            ((it.endDate!! - it.startDate) / (1000 * 60 * 60 * 24)).toInt() + 1
        }.filter { it in 1..15 }
        return if (durations.isNotEmpty()) durations.average().toInt() else null
    }

    // Predict next period start date
    suspend fun predictNextPeriodStart(): Long? {
        val latest = dao.getLatest() ?: return null
        val cycleLength = getAverageCycleLength() ?: 28
        return latest.startDate + cycleLength * 24L * 60 * 60 * 1000
    }

    // Get current cycle day
    suspend fun getCurrentCycleDay(): Int? {
        val latest = dao.getLatest() ?: return null
        val now = System.currentTimeMillis()
        val diff = ((now - latest.startDate) / (1000 * 60 * 60 * 24)).toInt()
        return if (diff >= 0) diff + 1 else null
    }

    // Get cycle lengths for analysis
    suspend fun getCycleLengths(): List<Int> {
        val records = dao.getLastN(12)
        if (records.size < 2) return emptyList()

        val cycles = mutableListOf<Int>()
        for (i in 0 until records.size - 1) {
            val diff = ((records[i].startDate - records[i + 1].startDate) / (1000 * 60 * 60 * 24)).toInt()
            if (diff in 15..45) cycles.add(diff)
        }
        return cycles
    }
}
