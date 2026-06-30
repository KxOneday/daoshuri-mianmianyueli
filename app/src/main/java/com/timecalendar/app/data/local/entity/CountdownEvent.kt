package com.timecalendar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countdown_events")
data class CountdownEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetDate: Long, // epoch millis
    val isCountdown: Boolean = true, // true=倒数, false=正数
    val category: String = "其他",
    val iconRes: String = "ic_star",
    val bgColor: String = "#FF6B9D",
    val bgImageUri: String? = null,
    val isPinned: Boolean = false,
    val isHidden: Boolean = false,
    val isRepeatYearly: Boolean = false,
    val isRepeatMonthly: Boolean = false,
    val useLunar: Boolean = false,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
