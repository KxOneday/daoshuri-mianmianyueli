package com.timecalendar.app.ui.screens.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timecalendar.app.data.local.entity.CountdownEvent
import com.timecalendar.app.ui.theme.CategoryColors
import com.timecalendar.app.util.DateUtils
import com.timecalendar.app.viewmodel.CountdownViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCountdownScreen(
    viewModel: CountdownViewModel,
    eventId: Long = -1L,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000) }
    var isCountdown by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("生活") }
    var selectedColor by remember { mutableStateOf("#FF6B9D") }
    var isPinned by remember { mutableStateOf(false) }
    var useLunar by remember { mutableStateOf(false) }
    var isRepeatYearly by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    // Load existing event if editing
    LaunchedEffect(eventId) {
        if (eventId > 0) {
            viewModel.getEventById(eventId)?.let { event ->
                title = event.title
                selectedDate = event.targetDate
                isCountdown = event.isCountdown
                selectedCategory = event.category
                selectedColor = event.bgColor
                isPinned = event.isPinned
                useLunar = event.useLunar
                isRepeatYearly = event.isRepeatYearly
                note = event.note
                isEditing = true
            }
        }
    }

    val categories = listOf("工作", "生活", "健康", "学习", "节日", "其他")
    val colors = listOf(
        "#FF6B9D", "#2196F3", "#4CAF50", "#FF9800",
        "#9C27B0", "#009688", "#E91E63", "#3F51B5",
        "#FF5722", "#607D8B", "#795548", "#00BCD4"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑倒数日" else "添加倒数日") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                val event = CountdownEvent(
                                    id = if (isEditing) eventId else 0,
                                    title = title.trim(),
                                    targetDate = selectedDate,
                                    isCountdown = isCountdown,
                                    category = selectedCategory,
                                    bgColor = selectedColor,
                                    isPinned = isPinned,
                                    useLunar = useLunar,
                                    isRepeatYearly = isRepeatYearly,
                                    note = note.trim()
                                )
                                if (isEditing) viewModel.updateEvent(event)
                                else viewModel.addEvent(event)
                                onSaved()
                            }
                        },
                        enabled = title.isNotBlank()
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
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("事件名称") },
                placeholder = { Text("例如：生日、考试、旅行...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Edit, null) }
            )

            // Date
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("目标日期", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            DateUtils.formatDisplay(selectedDate),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(Icons.Filled.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            // Mode toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = isCountdown,
                    onClick = { isCountdown = true },
                    label = { Text("倒数") },
                    leadingIcon = if (isCountdown) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = !isCountdown,
                    onClick = { isCountdown = false },
                    label = { Text("正数") },
                    leadingIcon = if (!isCountdown) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
            }

            // Category
            Column {
                Text("分类", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        val color = CategoryColors[category] ?: Color.Gray
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.15f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }
            }

            // Color
            Column {
                Text("颜色", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(colors) { colorHex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (selectedColor == colorHex) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == colorHex) {
                                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Options
            Column {
                Text("选项", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isPinned,
                        onClick = { isPinned = !isPinned },
                        label = { Text("置顶") },
                        leadingIcon = if (isPinned) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = useLunar,
                        onClick = { useLunar = !useLunar },
                        label = { Text("农历") },
                        leadingIcon = if (useLunar) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isRepeatYearly,
                        onClick = { isRepeatYearly = !isRepeatYearly },
                        label = { Text("每年重复") },
                        leadingIcon = if (isRepeatYearly) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注") },
                placeholder = { Text("可选，添加备注信息...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
        }
    }

    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
