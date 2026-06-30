package com.timecalendar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timecalendar.app.util.LunarCalendar
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * 滚轮式日期选择器
 * 公历模式：选择公历年/月/日
 * 农历模式：直接选择农历年/月/日，自动转为公历存储
 */
@Composable
fun WheelDatePicker(
    initialYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    initialMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    initialDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    useLunar: Boolean = false,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit
) {
    if (useLunar) {
        LunarWheelDatePicker(initialYear, initialMonth, initialDay, onDateSelected)
    } else {
        SolarWheelDatePicker(initialYear, initialMonth, initialDay, onDateSelected)
    }
}

/**
 * 公历滚轮选择器
 */
@Composable
private fun SolarWheelDatePicker(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int,
    onDateSelected: (Int, Int, Int) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth) }
    var selectedDay by remember { mutableIntStateOf(initialDay) }

    val years = (1901..2049).toList()
    val months = (1..12).toList()
    val daysInMonth = getSolarDaysInMonth(selectedYear, selectedMonth)
    val days = (1..daysInMonth).toList()

    LaunchedEffect(selectedYear, selectedMonth) {
        if (selectedDay > daysInMonth) selectedDay = daysInMonth
        onDateSelected(selectedYear, selectedMonth, selectedDay)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 预览
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${selectedYear}年${selectedMonth}月${selectedDay}日",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                )
                val lunar = try { LunarCalendar.solarToLunar(selectedYear, selectedMonth, selectedDay) } catch (_: Exception) { null }
                if (lunar != null) {
                    Text(lunar.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            WheelColumn(items = years, selected = selectedYear, onSelect = { selectedYear = it }, label = "年", modifier = Modifier.weight(1f))
            WheelColumn(items = months, selected = selectedMonth, onSelect = { selectedMonth = it }, label = "月", modifier = Modifier.weight(1f))
            WheelColumn(items = days, selected = selectedDay, onSelect = { selectedDay = it }, label = "日", modifier = Modifier.weight(1f))
        }
    }
}

/**
 * 农历滚轮选择器 - 直接选择农历月/日
 */
@Composable
private fun LunarWheelDatePicker(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int,
    onDateSelected: (Int, Int, Int) -> Unit
) {
    // 先把初始公历日期转为农历
    val initialLunar = try { LunarCalendar.solarToLunar(initialYear, initialMonth, initialDay) } catch (_: Exception) { null }

    var lunarYear by remember { mutableIntStateOf(initialLunar?.year ?: initialYear) }
    var lunarMonth by remember { mutableIntStateOf(initialLunar?.month ?: initialMonth) }
    var lunarDay by remember { mutableIntStateOf(initialLunar?.day ?: initialDay) }
    var isLeapMonth by remember { mutableStateOf(initialLunar?.isLeapMonth ?: false) }

    val years = (1901..2049).toList()

    // 让 lunarMonth 始终是正月=1 到腊月=12
    // 如果当前年有闰月，显示 [正月, 二月, ..., 闰X月, X+1月, ..., 腊月]
    val leapMonth = LunarCalendar.getLeapMonth(lunarYear)
    val monthCount = LunarCalendar.getMonthCount(lunarYear)
    val monthItems = remember(lunarYear) {
        val items = mutableListOf<Pair<Int, Boolean>>() // month, isLeap
        for (m in 1..12) {
            items.add(m to false)
            if (m == leapMonth) {
                items.add(m to true) // 闰月
            }
        }
        items
    }

    // 当前月天数
    val daysInMonth = if (isLeapMonth) {
        LunarCalendar.getLeapMonthDays(lunarYear)
    } else {
        LunarCalendar.getMonthDays(lunarYear, lunarMonth)
    }
    val days = (1..daysInMonth).toList()

    // 当选择变化时，转为公历回调
    LaunchedEffect(lunarYear, lunarMonth, lunarDay, isLeapMonth) {
        val solarMillis = LunarCalendar.lunarToSolar(lunarYear, lunarMonth, lunarDay, isLeapMonth)
        val cal = Calendar.getInstance().apply { timeInMillis = solarMillis }
        onDateSelected(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 预览 - 显示农历
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val lunar = try { LunarCalendar.solarToLunar(
                    Calendar.getInstance().apply { timeInMillis = LunarCalendar.lunarToSolar(lunarYear, lunarMonth, lunarDay, isLeapMonth) }.get(Calendar.YEAR),
                    Calendar.getInstance().apply { timeInMillis = LunarCalendar.lunarToSolar(lunarYear, lunarMonth, lunarDay, isLeapMonth) }.get(Calendar.MONTH) + 1,
                    Calendar.getInstance().apply { timeInMillis = LunarCalendar.lunarToSolar(lunarYear, lunarMonth, lunarDay, isLeapMonth) }.get(Calendar.DAY_OF_MONTH)
                ) } catch (_: Exception) { null }

                val ganZhi = try {
                    val gIdx = (lunarYear - 4) % 10; val zIdx = (lunarYear - 4) % 12
                    val gan = arrayOf("甲","乙","丙","丁","戊","己","庚","辛","壬","癸")
                    val zhi = arrayOf("子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥")
                    val sx = arrayOf("鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪")
                    "${gan[gIdx.coerceIn(0,9)]}${zhi[zIdx.coerceIn(0,11)]}${sx[zIdx.coerceIn(0,11)]}年"
                } catch (_: Exception) { "" }

                val monthNames = arrayOf("正月","二月","三月","四月","五月","六月","七月","八月","九月","十月","冬月","腊月")
                val dayNames = arrayOf("初一","初二","初三","初四","初五","初六","初七","初八","初九","初十","十一","十二","十三","十四","十五","十六","十七","十八","十九","二十","廿一","廿二","廿三","廿四","廿五","廿六","廿七","廿八","廿九","三十")
                val leapStr = if (isLeapMonth) "闰" else ""
                val mIdx = (lunarMonth - 1).coerceIn(0, 11)
                val dIdx = (lunarDay - 1).coerceIn(0, 29)

                Text(
                    "$ganZhi ${leapStr}${monthNames[mIdx]}${dayNames[dIdx]}",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 年/月/日 滚轮
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            // 年
            WheelColumn(items = years, selected = lunarYear, onSelect = { lunarYear = it }, label = "年", modifier = Modifier.weight(1f))

            // 月（带闰月）
            val monthLabels = monthItems.map { (m, isLeap) ->
                val names = arrayOf("正月","二月","三月","四月","五月","六月","七月","八月","九月","十月","冬月","腊月")
                if (isLeap) "闰${names[m - 1]}" else names[m - 1]
            }
            val selectedMonthIdx = monthItems.indexOfFirst { it.first == lunarMonth && it.second == isLeapMonth }.coerceAtLeast(0)

            WheelColumnGeneric(
                items = monthLabels,
                selectedIndex = selectedMonthIdx,
                onSelect = { idx ->
                    val (m, l) = monthItems[idx]
                    lunarMonth = m
                    isLeapMonth = l
                    // 修正日期
                    val maxDays = if (l) LunarCalendar.getLeapMonthDays(lunarYear) else LunarCalendar.getMonthDays(lunarYear, m)
                    if (lunarDay > maxDays) lunarDay = maxDays
                },
                label = "月",
                modifier = Modifier.weight(1.2f)
            )

            // 日
            WheelColumn(items = days, selected = lunarDay, onSelect = { lunarDay = it }, label = "日", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun WheelColumn(
    items: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    WheelColumnGeneric(
        items = items.map { "$it" },
        selectedIndex = items.indexOf(selected).coerceAtLeast(0),
        onSelect = { onSelect(items[it]) },
        label = label,
        modifier = modifier
    )
}

@Composable
private fun WheelColumnGeneric(
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceAtLeast(0))
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.height(180.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(36.dp).align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
            )

            LazyColumn(
                state = listState, modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 72.dp)
            ) {
                items(items.size) { index ->
                    val isSelected = index == selectedIndex
                    Box(modifier = Modifier.fillMaxWidth().height(36.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = items[index],
                            fontSize = if (isSelected) 18.sp else 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            LaunchedEffect(listState.isScrollInProgress) {
                if (!listState.isScrollInProgress) {
                    val centerIdx = (listState.firstVisibleItemIndex +
                            if (listState.firstVisibleItemScrollOffset > 50) 1 else 0)
                        .coerceIn(0, items.size - 1)
                    onSelect(centerIdx)
                    coroutineScope.launch { listState.animateScrollToItem(centerIdx) }
                }
            }
        }
    }
}

private fun getSolarDaysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}
