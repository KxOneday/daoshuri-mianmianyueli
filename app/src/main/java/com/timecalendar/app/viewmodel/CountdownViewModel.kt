package com.timecalendar.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timecalendar.app.data.local.AppDatabase
import com.timecalendar.app.data.local.entity.CountdownEvent
import com.timecalendar.app.data.repository.CountdownRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CountdownViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CountdownRepository
    val allEvents: StateFlow<List<CountdownEvent>>
    val pinnedEvents: StateFlow<List<CountdownEvent>>
    val categories: StateFlow<List<String>>

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CountdownRepository(db.countdownEventDao())

        allEvents = repository.getAllVisible()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        pinnedEvents = repository.getPinned()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        categories = repository.getAllCategories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getEventsByCategory(category: String): Flow<List<CountdownEvent>> {
        return repository.getByCategory(category)
    }

    fun getEventsByDateRange(startDate: Long, endDate: Long): Flow<List<CountdownEvent>> {
        return repository.getByDateRange(startDate, endDate)
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun addEvent(event: CountdownEvent) {
        viewModelScope.launch {
            repository.insert(event)
        }
    }

    fun updateEvent(event: CountdownEvent) {
        viewModelScope.launch {
            repository.update(event.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteEvent(event: CountdownEvent) {
        viewModelScope.launch {
            repository.delete(event)
        }
    }

    fun deleteEventById(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    suspend fun getEventById(id: Long): CountdownEvent? {
        return repository.getById(id)
    }
}
