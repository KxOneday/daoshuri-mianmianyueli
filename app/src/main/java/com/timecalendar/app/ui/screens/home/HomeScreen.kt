@file:OptIn(ExperimentalMaterial3Api::class)

package com.timecalendar.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timecalendar.app.data.local.entity.CountdownEvent
import com.timecalendar.app.ui.theme.*
import com.timecalendar.app.util.DateUtils
import com.timecalendar.app.util.LunarCalendar
import com.timecalendar.app.viewmodel.CountdownViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    countdownViewModel: CountdownViewModel,
    onDateClick: (Long) -> Unit = {}
) {
    val events by countdownViewModel.allEvents.collectAsState()
    val calendar = remember { Calendar.getInstance() }
    var currentYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Month selector
        MonthSelector(
            year = currentYear,
            month = currentMonth,
            onPrevious = {
                if (currentMonth == 1) {
                    currentYear--
                    currentMonth = 12
                } else {
                    currentMonth--
                }
            },
            onNext = {
                if (currentMonth == 12) {
                    currentYear++
                    currentMonth = 1
                } else {
                    currentMonth++
                }
            },
            onToday = {
                currentYear = calendar.get(Calendar.YEAR)
                currentMonth = calendar.get(Calendar.MONTH) + 1
                selectedDate = System.currentTimeMillis()
            }
        )

        // Week headers
        WeekHeaders()

        // Calendar grid
        CalendarGrid(
            year = currentYear,
            month = currentMonth,
            selectedDate = selectedDate,
            events = events,
            onDateClick = { dateMillis ->
                selectedDate = dateMillis
                onDateClick(dateMillis)
            }
        )

        // Selected date details
        SelectedDateDetails(
            dateMillis = selectedDate,
            events = events.filter { event ->
                val eventDay = DateUtils.getDayStart(event.targetDate)
                val selectedDay = DateUtils.getDayStart(selectedDate)
                eventDay == selectedDay || (event.isRepeatYearly &&
                        DateUtils.getMonth(event.targetDate) == DateUtils.getMonth(selectedDate) &&
                        DateUtils.getDay(event.targetDate) == DateUtils.getDay(selectedDate))
            }
        )
    }
}

@Composable
private fun MonthSelector(
    year: Int,
    month: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.ChevronLeft, "上月")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${year}年${month}月",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onToday) {
                Text("今天", fontSize = 12.sp)
            }
        }

        IconButton(onClick = onNext) {
            Icon(Icons.Filled.ChevronRight, "下月")
        }
    }
}

@Composable
private fun WeekHeaders() {
    val weekDays = listOf("一", "二", "三", "四", "五", "六", "日")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDays.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    selectedDate: Long,
    events: List<CountdownEvent>,
    onDateClick: (Long) -> Unit
) {
    val daysInMonth = DateUtils.getDaysInMonth(year, month)
    val firstDayOfWeek = DateUtils.getFirstDayOfWeek(year, month)
    val today = DateUtils.getTodayStart()
    val selectedDay = DateUtils.getDayStart(selectedDate)

    val totalCells = firstDayOfWeek + daysInMonth
    val rows = (totalCells + 6) / 7

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .height((rows * 52).dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(totalCells) { index ->
            if (index < firstDayOfWeek) {
                Spacer(modifier = Modifier.size(52.dp))
            } else {
                val day = index - firstDayOfWeek + 1
                val dateMillis = DateUtils.getDayStart(
                    DateUtils.getMonthStart(year, month) + (day - 1).toLong() * 24 * 60 * 60 * 1000
                )

                val isToday = dateMillis == today
                val isSelected = dateMillis == selectedDay
                val hasEvent = events.any { event ->
                    val eventDay = DateUtils.getDayStart(event.targetDate)
                    eventDay == dateMillis || (event.isRepeatYearly &&
                            DateUtils.getMonth(event.targetDate) == month &&
                            DateUtils.getDay(event.targetDate) == day)
                }

                val lunarDay = try {
                    LunarCalendar.solarToLunar(year, month, day).dayName
                } catch (e: Exception) {
                    ""
                }

                DayCell(
                    day = day,
                    lunarDay = lunarDay,
                    isToday = isToday,
                    isSelected = isSelected,
                    hasEvent = hasEvent,
                    onClick = { onDateClick(dateMillis) }
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    lunarDay: String,
    isToday: Boolean,
    isSelected: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$day",
            fontSize = 16.sp,
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
        Text(
            text = lunarDay,
            fontSize = 9.sp,
            color = if (isSelected) textColor.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        if (hasEvent) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) textColor else MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun SelectedDateDetails(
    dateMillis: Long,
    events: List<CountdownEvent>
) {
    val lunarInfo = try {
        val y = DateUtils.getYear(dateMillis)
        val m = DateUtils.getMonth(dateMillis)
        val d = DateUtils.getDay(dateMillis)
        LunarCalendar.solarToLunar(y, m, d)
    } catch (e: Exception) {
        null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = DateUtils.formatDisplay(dateMillis),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (lunarInfo != null) {
                        Text(
                            text = lunarInfo.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (DateUtils.isToday(dateMillis)) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("今天", fontSize = 11.sp) }
                    )
                }
            }

            if (events.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                events.forEach { event ->
                    EventChip(event)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "这一天没有事件",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EventChip(event: CountdownEvent) {
    val effectiveDate = DateUtils.getEffectiveTargetDate(event.targetDate, event.isRepeatYearly)
    val days = DateUtils.getDaysFromNow(effectiveDate)
    val color = try {
        Color(android.graphics.Color.parseColor(event.bgColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
        Text(
            text = DateUtils.daysUntilText(days),
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}
