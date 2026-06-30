package com.timecalendar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period_records")
data class PeriodRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: Long, // epoch millis
    val endDate: Long? = null,
    val flow: Int = 2, // 1=少, 2=中, 3=多
    val painLevel: Int = 0, // 0=无, 1=轻, 2=中, 3=重
    val mood: String = "", // 开心/平静/烦躁/低落
    val symptoms: String = "", // JSON array of symptom tags
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
