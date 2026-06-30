package com.timecalendar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_records")
data class HabitRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitName: String,
    val date: Long, // epoch millis (day start)
    val isCompleted: Boolean = false,
    val category: String = "健康", // 健康/运动/饮食/睡眠
    val iconRes: String = "ic_habit",
    val createdAt: Long = System.currentTimeMillis()
)
