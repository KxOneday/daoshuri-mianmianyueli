package com.timecalendar.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timecalendar.app.data.local.AppDatabase
import com.timecalendar.app.data.local.entity.HabitRecord
import com.timecalendar.app.data.local.entity.PeriodRecord
import com.timecalendar.app.data.repository.HabitRepository
import com.timecalendar.app.data.repository.PeriodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val periodRepository: PeriodRepository
    private val habitRepository: HabitRepository

    val allPeriods: StateFlow<List<PeriodRecord>>
    val recentPeriods: StateFlow<List<PeriodRecord>>
    val periodCount: StateFlow<Int>

    private val _averageCycle = MutableStateFlow<Int?>(null)
    val averageCycle: StateFlow<Int?> = _averageCycle

    private val _averageDuration = MutableStateFlow<Int?>(null)
    val averageDuration: StateFlow<Int?> = _averageDuration

    private val _nextPeriodStart = MutableStateFlow<Long?>(null)
    val nextPeriodStart: StateFlow<Long?> = _nextPeriodStart

    private val _currentCycleDay = MutableStateFlow<Int?>(null)
    val currentCycleDay: StateFlow<Int?> = _currentCycleDay

    private val _cycleLengths = MutableStateFlow<List<Int>>(emptyList())
    val cycleLengths: StateFlow<List<Int>> = _cycleLengths

    init {
        val db = AppDatabase.getDatabase(application)
        periodRepository = PeriodRepository(db.periodRecordDao())
        habitRepository = HabitRepository(db.habitRecordDao())

        allPeriods = periodRepository.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        recentPeriods = periodRepository.getRecent(6)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        periodCount = periodRepository.getCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

        refreshAnalysis()
    }

    fun refreshAnalysis() {
        viewModelScope.launch {
            _averageCycle.value = periodRepository.getAverageCycleLength()
            _averageDuration.value = periodRepository.getAveragePeriodDuration()
            _nextPeriodStart.value = periodRepository.predictNextPeriodStart()
            _currentCycleDay.value = periodRepository.getCurrentCycleDay()
            _cycleLengths.value = periodRepository.getCycleLengths()
        }
    }

    // Period Record operations
    fun addPeriod(record: PeriodRecord) {
        viewModelScope.launch {
            periodRepository.insert(record)
            refreshAnalysis()
        }
    }

    fun updatePeriod(record: PeriodRecord) {
        viewModelScope.launch {
            periodRepository.update(record)
            refreshAnalysis()
        }
    }

    fun deletePeriod(record: PeriodRecord) {
        viewModelScope.launch {
            periodRepository.delete(record)
            refreshAnalysis()
        }
    }

    suspend fun getPeriodById(id: Long): PeriodRecord? {
        return periodRepository.getById(id)
    }

    // Habit operations
    fun getHabitsByDate(date: Long): Flow<List<HabitRecord>> {
        return habitRepository.getByDate(date)
    }

    fun getHabitsByDateRange(startDate: Long, endDate: Long): Flow<List<HabitRecord>> {
        return habitRepository.getByDateRange(startDate, endDate)
    }

    fun addHabit(record: HabitRecord) {
        viewModelScope.launch {
            habitRepository.insert(record)
        }
    }

    fun toggleHabit(record: HabitRecord) {
        viewModelScope.launch {
            habitRepository.update(record.copy(isCompleted = !record.isCompleted))
        }
    }

    fun deleteHabit(record: HabitRecord) {
        viewModelScope.launch {
            habitRepository.delete(record)
        }
    }
}
