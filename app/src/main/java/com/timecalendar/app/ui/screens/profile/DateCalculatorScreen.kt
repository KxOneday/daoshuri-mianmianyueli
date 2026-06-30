@file:OptIn(ExperimentalMaterial3Api::class)

package com.timecalendar.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timecalendar.app.ui.theme.CountdownBlue
import com.timecalendar.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("日期间隔", "N天后日期")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日期计算器") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                0 -> DateBetweenSection()
                1 -> DateAfterSection()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateBetweenSection() {
    var date1 by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var date2 by remember { mutableLongStateOf(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) }
    var showPicker1 by remember { mutableStateOf(false) }
    var showPicker2 by remember { mutableStateOf(false) }

    val days = DateUtils.getDaysBetween(date1, date2)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = { showPicker1 = true }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("开始日期", style = MaterialTheme.typography.labelMedium)
                    Text(DateUtils.formatDisplay(date1), fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Filled.CalendarMonth, null, tint = CountdownBlue)
            }
        }

        Icon(
            Icons.Filled.SwapVert,
            null,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = { showPicker2 = true }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("结束日期", style = MaterialTheme.typography.labelMedium)
                    Text(DateUtils.formatDisplay(date2), fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Filled.CalendarMonth, null, tint = CountdownBlue)
            }
        }

        // Result
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CountdownBlue.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("间隔", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "$days",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = CountdownBlue
                )
                Text("天", color = CountdownBlue)
                Spacer(modifier = Modifier.height(8.dp))
                val weeks = days / 7
                val months = days / 30
                Text(
                    "约 $weeks 周 · 约 $months 月",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showPicker1) {
        val state = rememberDatePickerState(initialSelectedDateMillis = date1)
        DatePickerDialog(
            onDismissRequest = { showPicker1 = false },
            confirmButton = { TextButton(onClick = { state.selectedDateMillis?.let { date1 = it }; showPicker1 = false }) { Text("确定") } },
            dismissButton = { TextButton(onClick = { showPicker1 = false }) { Text("取消") } }
        ) { DatePicker(state = state) }
    }
    if (showPicker2) {
        val state = rememberDatePickerState(initialSelectedDateMillis = date2)
        DatePickerDialog(
            onDismissRequest = { showPicker2 = false },
            confirmButton = { TextButton(onClick = { state.selectedDateMillis?.let { date2 = it }; showPicker2 = false }) { Text("确定") } },
            dismissButton = { TextButton(onClick = { showPicker2 = false }) { Text("取消") } }
        ) { DatePicker(state = state) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateAfterSection() {
    var baseDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var daysInput by remember { mutableStateOf("30") }
    var showPicker by remember { mutableStateOf(false) }

    val days = daysInput.toIntOrNull() ?: 0
    val resultDate = DateUtils.addDays(baseDate, days)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = { showPicker = true }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("起始日期", style = MaterialTheme.typography.labelMedium)
                    Text(DateUtils.formatDisplay(baseDate), fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Filled.CalendarMonth, null, tint = CountdownBlue)
            }
        }

        OutlinedTextField(
            value = daysInput,
            onValueChange = { daysInput = it.filter { c -> c.isDigit() || c == '-' } },
            label = { Text("天数") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.DateRange, null) },
            suffix = { Text("天") }
        )

        // Result
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CountdownBlue.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (days >= 0) "$days 天后是" else "${-days} 天前是",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    DateUtils.formatDisplay(resultDate),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CountdownBlue
                )
                val lunar = try {
                    com.timecalendar.app.util.LunarCalendar.getLunarDateString(
                        DateUtils.getYear(resultDate),
                        DateUtils.getMonth(resultDate),
                        DateUtils.getDay(resultDate)
                    )
                } catch (e: Exception) { "" }
                if (lunar.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(lunar, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = baseDate)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = { TextButton(onClick = { state.selectedDateMillis?.let { baseDate = it }; showPicker = false }) { Text("确定") } },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("取消") } }
        ) { DatePicker(state = state) }
    }
}
