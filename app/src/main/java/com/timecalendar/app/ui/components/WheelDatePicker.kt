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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timecalendar.app.util.LunarCalendar
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun WheelDatePicker(
    initialYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    initialMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    initialDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    useLunar: Boolean = false,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth) }
    var selectedDay by remember { mutableIntStateOf(initialDay) }

    val years = (1900..2100).toList()
    val months = (1..12).toList()
    val daysInMonth = getDaysInMonth(selectedYear, selectedMonth)
    val days = (1..daysInMonth).toList()

    // Clamp day if needed
    LaunchedEffect(selectedYear, selectedMonth) {
        if (selectedDay > daysInMonth) selectedDay = daysInMonth
        onDateSelected(selectedYear, selectedMonth, selectedDay)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$selectedYear 年 $selectedMonth 月 $selectedDay 日",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (useLunar) {
                    val lunar = try {
                        LunarCalendar.getLunarDateString(selectedYear, selectedMonth, selectedDay)
                    } catch (_: Exception) { "" }
                    if (lunar.isNotBlank()) {
                        Text(
                            text = lunar,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wheel selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Year wheel
            WheelSelector(
                items = years,
                selectedItem = selectedYear,
                onItemSelected = { selectedYear = it },
                label = "年",
                modifier = Modifier.weight(1f),
                itemLabel = { "$it" }
            )

            // Month wheel
            WheelSelector(
                items = months,
                selectedItem = selectedMonth,
                onItemSelected = { selectedMonth = it },
                label = "月",
                modifier = Modifier.weight(1f),
                itemLabel = { "$it" }
            )

            // Day wheel
            WheelSelector(
                items = days,
                selectedItem = selectedDay,
                onItemSelected = { selectedDay = it },
                label = "日",
                modifier = Modifier.weight(1f),
                itemLabel = { "$it" }
            )
        }
    }
}

@Composable
private fun <T> WheelSelector(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    itemLabel: (T) -> String = { it.toString() }
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = items.indexOf(selectedItem).coerceAtLeast(0)
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // Center highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.Center)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        RoundedCornerShape(8.dp)
                    )
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 80.dp)
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    val isSelected = item == selectedItem

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = itemLabel(item),
                            fontSize = if (isSelected) 20.sp else 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Scroll detection - snap to selected
            LaunchedEffect(listState.isScrollInProgress) {
                if (!listState.isScrollInProgress) {
                    val centerIndex = listState.firstVisibleItemIndex +
                            if (listState.firstVisibleItemScrollOffset > 60) 1 else 0
                    val clampedIndex = centerIndex.coerceIn(0, items.size - 1)
                    onItemSelected(items[clampedIndex])
                    coroutineScope.launch {
                        listState.animateScrollToItem(clampedIndex)
                    }
                }
            }
        }
    }
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}
