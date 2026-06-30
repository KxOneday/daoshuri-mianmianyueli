package com.timecalendar.app.util

import java.util.Calendar

/**
 * 农历万年历 - 基于标准农历数据表（1901-2049）
 * 数据来源：广泛验证的农历数据表
 */
object LunarCalendar {

    // 标准农历数据表（1901-2049）
    // 编码方式：低4位=闰月月份（0=无闰月），4-15位=每月大小（1=30天，0=29天），16位=闰月大小
    private val LUNAR_INFO = intArrayOf(
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
        0x06566, 0x0d4a0, 0x0ea50, 0x16a95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
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

    // 正月初一对应的公历日期（1901-2049）
    // 格式：月*100+日，年份从1901开始
    private val SPRING_FESTIVAL = intArrayOf(
        219, 208, 129, 216, 204, 125, 213, 202, 122, 210, // 1901-1910
        130, 218, 206, 126, 214, 204, 123, 211, 131, 219, // 1911-1920
        208, 128, 216, 205, 124, 213, 202, 123, 210, 130, // 1921-1930
        217, 206, 126, 214, 204, 124, 211, 131, 219, 208, // 1931-1940
        127, 215, 205, 125, 213, 202, 122, 210, 129, 217, // 1941-1950
        206, 126, 214, 203, 124, 212, 131, 218, 207, 128, // 1951-1960
        215, 205, 125, 213, 202, 121, 209, 130, 217, 206, // 1961-1970
        127, 215, 203, 123, 211, 131, 218, 207, 126, 215, // 1971-1980
        204, 123, 210, 130, 217, 206, 127, 214, 203, 123, // 1981-1990
        210, 129, 217, 206, 126, 214, 203, 122, 210, 129, // 1991-2000
        216, 205, 124, 212, 131, 219, 207, 126, 214, 203, // 2001-2010
        123, 210, 129, 217, 205, 126, 214, 202, 122, 209, // 2011-2020
        128, 216, 205, 124, 212, 131, 219, 207, 126, 214, // 2021-2030
        203, 123, 210, 129, 217, 205, 125, 213, 202, 122, // 2031-2040
        210, 128, 216, 205, 124, 212, 131, 219, 207, 126  // 2041-2049
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
            return "${yearGanZhi}${shengXiao}年 ${leap}${monthName}${dayName}"
        }
    }

    /** 获取农历某月天数 */
    private fun monthDays(year: Int, month: Int): Int {
        val idx = year - 1901
        if (idx < 0 || idx >= LUNAR_INFO.size) return 29
        return if (LUNAR_INFO[idx] and (0x10000 shr month) != 0) 30 else 29
    }

    /** 获取闰月月份（0=无闰月） */
    private fun leapMonth(year: Int): Int {
        val idx = year - 1901
        if (idx < 0 || idx >= LUNAR_INFO.size) return 0
        return LUNAR_INFO[idx] and 0xf
    }

    /** 获取闰月天数 */
    private fun leapMonthDays(year: Int): Int {
        val lm = leapMonth(year)
        if (lm == 0) return 0
        val idx = year - 1901
        return if (LUNAR_INFO[idx] and 0x10000 != 0) 30 else 29
    }

    /** 获取农历全年天数 */
    private fun yearDays(year: Int): Int {
        var sum = 348
        var i = 0x8000
        val idx = year - 1901
        if (idx < 0 || idx >= LUNAR_INFO.size) return 354
        while (i > 0x8) {
            if (LUNAR_INFO[idx] and i != 0) sum++
            i = i shr 1
        }
        return sum + leapMonthDays(year)
    }

    /**
     * 公历转农历
     */
    fun solarToLunar(year: Int, month: Int, day: Int): LunarDate {
        val idx = year - 1901
        if (idx < 0 || idx >= SPRING_FESTIVAL.size) {
            return fallbackLunarDate(year, month, day)
        }

        // 正月初一的公历日期
        val springInfo = SPRING_FESTIVAL[idx]
        val springMonth = springInfo / 100
        val springDay = springInfo % 100

        // 计算目标日期与正月初一的天数差
        val springCal = Calendar.getInstance().apply {
            set(year, springMonth - 1, springDay, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetCal = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var offset = ((targetCal.timeInMillis - springCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        var lunarYear: Int
        var lunarMonth: Int
        var lunarDay: Int
        var isLeap = false

        if (offset >= 0) {
            // 在当年正月初一之后
            lunarYear = year
        } else {
            // 在当年正月初一之前，属于上一年
            lunarYear = year - 1
            // 重新计算偏移：从上一年正月初一到目标日期
            val prevIdx = lunarYear - 1901
            if (prevIdx < 0 || prevIdx >= SPRING_FESTIVAL.size) {
                return fallbackLunarDate(year, month, day)
            }
            val prevSpring = SPRING_FESTIVAL[prevIdx]
            val prevCal = Calendar.getInstance().apply {
                set(lunarYear, prevSpring / 100 - 1, prevSpring % 100, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            offset = ((targetCal.timeInMillis - prevCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        }

        // 逐月计算
        val lm = leapMonth(lunarYear)
        var monthIdx = 1
        var daysInMonth: Int

        while (monthIdx <= 12) {
            daysInMonth = monthDays(lunarYear, monthIdx)
            if (offset < daysInMonth) break
            offset -= daysInMonth

            // 检查闰月
            if (lm > 0 && monthIdx == lm && !isLeap) {
                // 当前月有闰月，先处理闰月
                val leapDays = leapMonthDays(lunarYear)
                if (offset < leapDays) {
                    isLeap = true
                    break
                }
                offset -= leapDays
            }

            monthIdx++
        }

        if (isLeap) {
            lunarMonth = lm
            lunarDay = offset + 1
        } else {
            lunarMonth = monthIdx
            lunarDay = offset + 1
        }

        val ganIndex = (lunarYear - 4) % 10
        val zhiIndex = (lunarYear - 4) % 12

        return LunarDate(
            year = lunarYear,
            month = lunarMonth,
            day = lunarDay,
            isLeapMonth = isLeap,
            yearGanZhi = "${TIAN_GAN[ganIndex.coerceIn(0, 9)]}${DI_ZHI[zhiIndex.coerceIn(0, 11)]}",
            shengXiao = SHENG_XIAO[zhiIndex.coerceIn(0, 11)],
            monthName = LUNAR_MONTH_NAMES[(lunarMonth - 1).coerceIn(0, 11)],
            dayName = LUNAR_DAY_NAMES[(lunarDay - 1).coerceIn(0, 29)]
        )
    }

    /**
     * 农历转公历
     */
    fun lunarToSolar(year: Int, month: Int, day: Int, isLeapMonth: Boolean = false): Long {
        val idx = year - 1901
        if (idx < 0 || idx >= SPRING_FESTIVAL.size) return System.currentTimeMillis()

        val springInfo = SPRING_FESTIVAL[idx]
        val springCal = Calendar.getInstance().apply {
            set(year, springInfo / 100 - 1, springInfo % 100, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var offset = 0
        val lm = leapMonth(year)

        // 累加到目标月
        for (m in 1 until month) {
            offset += monthDays(year, m)
            if (lm == m) {
                offset += leapMonthDays(year)
            }
        }

        // 如果目标月有闰月且不是闰月，需要加上闰月天数
        if (lm > 0 && lm < month) {
            // 已经在循环中处理了
        }
        if (lm == month && !isLeapMonth && lm > 0) {
            // 目标是某月（非闰），但该月有闰月，闰月在前面
        }

        offset += day - 1

        return springCal.timeInMillis + offset.toLong() * 24 * 60 * 60 * 1000
    }

    /**
     * 获取农历某月天数（供外部使用）
     */
    fun getMonthDays(year: Int, month: Int): Int = monthDays(year, month)

    /**
     * 获取农历某年闰月（0=无闰月）
     */
    fun getLeapMonth(year: Int): Int = leapMonth(year)

    /**
     * 获取农历某年闰月天数
     */
    fun getLeapMonthDays(year: Int): Int = leapMonthDays(year)

    /**
     * 获取农历某年总月数（有闰月则13个月）
     */
    fun getMonthCount(year: Int): Int {
        return if (leapMonth(year) > 0) 13 else 12
    }

    fun getLunarDateString(year: Int, month: Int, day: Int): String {
        return try {
            solarToLunar(year, month, day).toString()
        } catch (e: Exception) { "" }
    }

    private fun fallbackLunarDate(year: Int, month: Int, day: Int): LunarDate {
        val ganIndex = (year - 4) % 10
        val zhiIndex = (year - 4) % 12
        return LunarDate(
            year = year, month = month, day = day, isLeapMonth = false,
            yearGanZhi = "${TIAN_GAN[ganIndex.coerceIn(0, 9)]}${DI_ZHI[zhiIndex.coerceIn(0, 11)]}",
            shengXiao = SHENG_XIAO[zhiIndex.coerceIn(0, 11)],
            monthName = LUNAR_MONTH_NAMES[(month - 1).coerceIn(0, 11)],
            dayName = LUNAR_DAY_NAMES[(day - 1).coerceIn(0, 29)]
        )
    }
}
