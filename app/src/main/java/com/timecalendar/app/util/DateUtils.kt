package com.timecalendar.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val displayFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
    private val monthFormat = SimpleDateFormat("yyyy年MM月", Locale.CHINA)
    private val dayFormat = SimpleDateFormat("MM月dd日", Locale.CHINA)

    fun formatDate(millis: Long): String = dateFormat.format(Date(millis))
    fun formatDisplay(millis: Long): String = displayFormat.format(Date(millis))
    fun formatMonth(millis: Long): String = monthFormat.format(Date(millis))
    fun formatDay(millis: Long): String = dayFormat.format(Date(millis))

    fun parseDate(dateStr: String): Long? {
        return try {
            dateFormat.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }

    fun getDaysBetween(date1: Long, date2: Long): Int {
        val diff = Math.abs(date2 - date1)
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

    fun getDaysFromNow(targetDate: Long): Int {
        val now = getTodayStart()
        return getDaysBetween(now, targetDate)
    }

    fun isFuture(targetDate: Long): Boolean {
        return targetDate > getTodayStart()
    }

    fun isToday(date: Long): Boolean {
        return getDayStart(date) == getTodayStart()
    }

    fun getTodayStart(): Long = getDayStart(System.currentTimeMillis())

    fun getDayStart(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getMonthStart(year: Int, month: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getMonthEnd(year: Int, month: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.timeInMillis
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday=0
    }

    fun addDays(date: Long, days: Int): Long {
        return date + days.toLong() * 24 * 60 * 60 * 1000
    }

    fun getYear(date: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        return cal.get(Calendar.YEAR)
    }

    fun getMonth(date: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        return cal.get(Calendar.MONTH) + 1
    }

    fun getDay(date: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    fun daysUntilText(days: Int): String {
        return when {
            days == 0 -> "就是今天！"
            days == 1 -> "明天"
            days == -1 -> "昨天"
            days > 0 -> "还有 $days 天"
            else -> "已过 ${-days} 天"
        }
    }
}
