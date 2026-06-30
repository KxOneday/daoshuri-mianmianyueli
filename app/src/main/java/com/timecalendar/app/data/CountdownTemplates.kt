package com.timecalendar.app.data

data class CountdownTemplate(
    val title: String,
    val category: String,
    val bgColor: String,
    val isRepeatYearly: Boolean = false,
    val useLunar: Boolean = false,
    val isCountdown: Boolean = true
)

object CountdownTemplates {

    val templates = listOf(
        CountdownTemplate("生日", "生日", "#E91E63", isRepeatYearly = true),
        CountdownTemplate("恋爱纪念日", "恋爱", "#FF4081", isRepeatYearly = true),
        CountdownTemplate("结婚纪念日", "恋爱", "#E040FB", isRepeatYearly = true),
        CountdownTemplate("高考", "学习", "#2196F3"),
        CountdownTemplate("考研", "学习", "#3F51B5"),
        CountdownTemplate("发工资", "工作", "#4CAF50"),
        CountdownTemplate("还信用卡", "工作", "#FF5722"),
        CountdownTemplate("春节", "节日", "#F44336", isRepeatYearly = true, useLunar = true),
        CountdownTemplate("中秋节", "节日", "#FF9800", isRepeatYearly = true, useLunar = true),
        CountdownTemplate("端午节", "节日", "#4CAF50", isRepeatYearly = true, useLunar = true),
        CountdownTemplate("旅行", "生活", "#00BCD4"),
        CountdownTemplate("婚礼", "生活", "#E040FB"),
        CountdownTemplate("宝宝出生", "生活", "#FFB74D"),
        CountdownTemplate("体检", "健康", "#F44336"),
        CountdownTemplate("减肥目标", "健康", "#FF9800")
    )
}
