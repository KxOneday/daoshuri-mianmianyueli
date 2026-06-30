package com.timecalendar.app.util

import java.util.Calendar

/**
 * Lunar calendar utility for Chinese calendar conversion.
 * Supports 1901-2049.
 */
object LunarCalendar {

    // Lunar calendar data from 1901 to 2049
    // Each entry encodes: leap month, month days, leap month days
    private val LUNAR_INFO = longArrayOf(
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
        0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x05ac0, 0x0ab60, 0x096d5, 0x092e0,
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
        0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0
    )

    private val LUNAR_MONTH_NAMES = arrayOf(
        "正月", "二月", "三月", "四月", "五月", "六月",
        "七月", "八月", "九月", "十月", "冬月", "腊月"
    )

    private val LUNAR_DAY_NAMES = arrayOf(
        "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
        "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
        "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
    )

    private val TIAN_GAN = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
    private val DI_ZHI = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
    private val SHENG_XIAO = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")

    data class LunarDate(
        val year: Int,
        val month: Int,
        val day: Int,
        val isLeapMonth: Boolean,
        val yearGanZhi: String,
        val shengXiao: String,
        val monthName: String,
        val dayName: String
    ) {
        override fun toString(): String {
            val leap = if (isLeapMonth) "闰" else ""
            return "$yearGanZhi${shengXiao}年 ${leap}$monthName$dayName"
        }
    }

    // Get the number of days in a lunar year
    private fun lunarYearDays(year: Int): Int {
        var sum = 348
        var i = 0x8000
        val info = LUNAR_INFO[year - 1901]
        while (i > 0x8) {
            if (info and i.toLong() != 0L) sum += 1
            i = i shr 1
        }
        return sum + leapMonthDays(year)
    }

    // Get the leap month number (0 if no leap month)
    private fun leapMonth(year: Int): Int {
        return (LUNAR_INFO[year - 1901] and 0xf).toInt()
    }

    // Get days in the leap month
    private fun leapMonthDays(year: Int): Int {
        return if (leapMonth(year) != 0) {
            if (LUNAR_INFO[year - 1901] and 0x10000 != 0L) 30 else 29
        } else 0
    }

    // Get days in a specific lunar month
    private fun monthDays(year: Int, month: Int): Int {
        return if (LUNAR_INFO[year - 1901] and (0x10000 shr month).toLong() != 0L) 30 else 29
    }

    // Convert solar date to lunar date
    fun solarToLunar(year: Int, month: Int, day: Int): LunarDate {
        val solarCal = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val baseCal = Calendar.getInstance().apply {
            set(1901, 0, 1, 0, 0, 0) // 1901-01-01 is 1900 lunar Jan 1
            set(Calendar.MILLISECOND, 0)
        }

        var offset = ((solarCal.timeInMillis - baseCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        // Adjust for the actual lunar start: 1901-02-19 is lunar 1901-01-01
        val lunarBase = Calendar.getInstance().apply {
            set(1901, 1, 19, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        offset = ((solarCal.timeInMillis - lunarBase.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        var lunarYear = 1901
        var daysInYear: Int

        while (lunarYear < 2050) {
            daysInYear = lunarYearDays(lunarYear)
            if (offset < daysInYear) break
            offset -= daysInYear
            lunarYear++
        }

        val leap = leapMonth(lunarYear)
        var isLeapMonth = false
        var lunarMonth = 1
        var daysInMonth: Int

        while (lunarMonth <= 12) {
            daysInMonth = if (leap > 0 && lunarMonth == leap + 1 && !isLeapMonth) {
                isLeapMonth = true
                leapMonthDays(lunarYear)
            } else {
                isLeapMonth = false
                monthDays(lunarYear, lunarMonth)
            }

            if (offset < daysInMonth) break
            offset -= daysInMonth

            if (!isLeapMonth) lunarMonth++
        }

        val lunarDay = offset + 1
        val ganIndex = (lunarYear - 4) % 10
        val zhiIndex = (lunarYear - 4) % 12

        return LunarDate(
            year = lunarYear,
            month = lunarMonth,
            day = lunarDay,
            isLeapMonth = isLeapMonth,
            yearGanZhi = "${TIAN_GAN[ganIndex]}${DI_ZHI[zhiIndex]}",
            shengXiao = SHENG_XIAO[zhiIndex],
            monthName = LUNAR_MONTH_NAMES[lunarMonth - 1],
            dayName = LUNAR_DAY_NAMES[lunarDay - 1]
        )
    }

    // Get lunar date string for display
    fun getLunarDateString(year: Int, month: Int, day: Int): String {
        return try {
            val lunar = solarToLunar(year, month, day)
            lunar.toString()
        } catch (e: Exception) {
            ""
        }
    }
}
