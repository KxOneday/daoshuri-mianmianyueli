package com.timecalendar.app.data

data class CountdownTemplate(
    val title: String,
    val category: String,
    val iconRes: String,
    val bgColor: String,
    val isRepeatYearly: Boolean = false,
    val useLunar: Boolean = false,
    val note: String = "",
    val isCountdown: Boolean = true
)

object CountdownTemplates {

    val templates = listOf(
        // 生日类
        CountdownTemplate("生日", "生日", "ic_cake", "#E91E63", isRepeatYearly = true),
        CountdownTemplate("家人生日", "生日", "ic_cake", "#FF6B6B", isRepeatYearly = true),
        CountdownTemplate("朋友生日", "生日", "ic_cake", "#FF9800", isRepeatYearly = true),

        // 恋爱类
        CountdownTemplate("恋爱纪念日", "恋爱", "ic_favorite", "#FF4081", isRepeatYearly = true),
        CountdownTemplate("结婚纪念日", "恋爱", "ic_favorite", "#E040FB", isRepeatYearly = true),
        CountdownTemplate("第一次约会", "恋爱", "ic_favorite", "#F50057", isCountdown = false),

        // 考试学习
        CountdownTemplate("高考", "学习", "ic_school", "#2196F3"),
        CountdownTemplate("考研", "学习", "ic_school", "#3F51B5"),
        CountdownTemplate("期末考试", "学习", "ic_school", "#00BCD4"),
        CountdownTemplate("英语四六级", "学习", "ic_school", "#009688"),
        CountdownTemplate("公务员考试", "学习", "ic_school", "#607D8B"),

        // 工作
        CountdownTemplate("发工资", "工作", "ic_work", "#4CAF50", isRepeatYearly = false),
        CountdownTemplate("还信用卡", "工作", "ic_work", "#FF5722"),
        CountdownTemplate("项目截止", "工作", "ic_work", "#795548"),
        CountdownTemplate("述职答辩", "工作", "ic_work", "#9E9E9E"),

        // 节日
        CountdownTemplate("春节", "节日", "ic_festival", "#F44336", isRepeatYearly = true, useLunar = true),
        CountdownTemplate("中秋节", "节日", "ic_festival", "#FF9800", isRepeatYearly = true, useLunar = true),
        CountdownTemplate("端午节", "节日", "ic_festival", "#4CAF50", isRepeatYearly = true, useLunar = true),
        CountdownTemplate("圣诞节", "节日", "ic_festival", "#E91E63", isRepeatYearly = true),
        CountdownTemplate("情人节", "节日", "ic_festival", "#FF4081", isRepeatYearly = true),
        CountdownTemplate("七夕", "节日", "ic_festival", "#9C27B0", isRepeatYearly = true, useLunar = true),

        // 生活
        CountdownTemplate("旅行", "生活", "ic_flight", "#00BCD4"),
        CountdownTemplate("搬家", "生活", "ic_home", "#795548"),
        CountdownTemplate("婚礼", "生活", "ic_favorite", "#E040FB"),
        CountdownTemplate("宝宝出生", "生活", "ic_child", "#FFB74D", isCountdown = false),
        CountdownTemplate("退伍", "生活", "ic_military", "#455A64"),

        // 健康
        CountdownTemplate("体检", "健康", "ic_health", "#F44336"),
        CountdownTemplate("疫苗接种", "健康", "ic_health", "#4CAF50"),
        CountdownTemplate("减肥目标", "健康", "ic_health", "#FF9800"),

        // 还款
        CountdownTemplate("房贷还款", "还款", "ic_money", "#607D8B"),
        CountdownTemplate("车贷还款", "还款", "ic_money", "#795548"),
        CountdownTemplate("花呗还款", "还款", "ic_money", "#FF5722")
    )

    val categories = templates.map { it.category }.distinct()

    fun getByCategory(category: String): List<CountdownTemplate> {
        return templates.filter { it.category == category }
    }
}
