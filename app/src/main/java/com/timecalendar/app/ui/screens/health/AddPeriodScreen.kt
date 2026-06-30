package com.timecalendar.app.ui.screens.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timecalendar.app.data.local.entity.PeriodRecord
import com.timecalendar.app.ui.theme.PeriodRed
import com.timecalendar.app.util.DateUtils
import com.timecalendar.app.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPeriodScreen(
    viewModel: HealthViewModel,
    recordId: Long = -1L,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var startDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hasEndDate by remember { mutableStateOf(false) }
    var flow by remember { mutableIntStateOf(2) }
    var painLevel by remember { mutableIntStateOf(0) }
    var mood by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val moods = listOf("😊 开心", "😌 平静", "😤 烦躁", "😢 低落", "😴 疲惫")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("记录经期") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val record = PeriodRecord(
                                startDate = startDate,
                                endDate = if (hasEndDate) endDate else null,
                                flow = flow,
                                painLevel = painLevel,
                                mood = mood,
                                notes = notes.trim()
                            )
                            viewModel.addPeriod(record)
                            onSaved()
                        }
                    ) {
                        Text("保存", fontWeight = FontWeight.Bold)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Start Date
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                onClick = { showStartPicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("开始日期", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            DateUtils.formatDisplay(startDate),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(Icons.Filled.CalendarMonth, null, tint = PeriodRed)
                }
            }

            // End Date Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("已结束")
                Switch(
                    checked = hasEndDate,
                    onCheckedChange = { hasEndDate = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = PeriodRed)
                )
            }

            if (hasEndDate) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { showEndPicker = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("结束日期", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                DateUtils.formatDisplay(endDate),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(Icons.Filled.CalendarMonth, null, tint = PeriodRed)
                    }
                }
            }

            // Flow
            Column {
                Text("流量", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1 to "少", 2 to "中", 3 to "多").forEach { (value, label) ->
                        FilterChip(
                            selected = flow == value,
                            onClick = { flow = value },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PeriodRed.copy(alpha = 0.15f),
                                selectedLabelColor = PeriodRed
                            )
                        )
                    }
                }
            }

            // Pain Level
            Column {
                Text("痛经程度", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0 to "无", 1 to "轻", 2 to "中", 3 to "重").forEach { (value, label) ->
                        FilterChip(
                            selected = painLevel == value,
                            onClick = { painLevel = value },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PeriodRed.copy(alpha = 0.15f),
                                selectedLabelColor = PeriodRed
                            )
                        )
                    }
                }
            }

            // Mood
            Column {
                Text("心情", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    moods.forEach { m ->
                        FilterChip(
                            selected = mood == m,
                            onClick = { mood = if (mood == m) "" else m },
                            label = { Text(m) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PeriodRed.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("备注") },
                placeholder = { Text("记录症状、感受等...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
        }
    }

    // Date Pickers
    if (showStartPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = startDate)
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { startDate = it }
                    showStartPicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("取消") }
            }
        ) { DatePicker(state = state) }
    }

    if (showEndPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = endDate)
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { endDate = it }
                    showEndPicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("取消") }
            }
        ) { DatePicker(state = state) }
    }
}
