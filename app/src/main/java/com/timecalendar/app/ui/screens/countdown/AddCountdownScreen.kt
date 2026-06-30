@file:OptIn(ExperimentalMaterial3Api::class)

package com.timecalendar.app.ui.screens.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timecalendar.app.data.CountdownTemplate
import com.timecalendar.app.data.CountdownTemplates
import com.timecalendar.app.data.local.entity.CountdownEvent
import com.timecalendar.app.reminder.ReminderManager
import com.timecalendar.app.ui.components.ColorSpectrumPicker
import com.timecalendar.app.ui.components.WheelDatePicker
import com.timecalendar.app.ui.theme.CategoryColors
import com.timecalendar.app.util.DateUtils
import com.timecalendar.app.viewmodel.CountdownViewModel

@Composable
fun AddCountdownScreen(
    viewModel: CountdownViewModel,
    eventId: Long = -1L,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000) }
    var isCountdown by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("生活") }
    var selectedColor by remember { mutableStateOf(Color(0xFFFF6B9D)) }
    var isPinned by remember { mutableStateOf(false) }
    var useLunar by remember { mutableStateOf(false) }
    var isRepeatYearly by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var showWheelDatePicker by remember { mutableStateOf(false) }
    var remindBefore by remember { mutableIntStateOf(0) }

    val allCategories = listOf("生日", "恋爱", "学习", "工作", "生活", "健康", "节日", "还款", "其他")

    LaunchedEffect(eventId) {
        if (eventId > 0) {
            viewModel.getEventById(eventId)?.let { event ->
                title = event.title
                selectedDate = event.targetDate
                isCountdown = event.isCountdown
                selectedCategory = event.category
                selectedColor = try { Color(android.graphics.Color.parseColor(event.bgColor)) } catch (_: Exception) { Color(0xFFFF6B9D) }
                isPinned = event.isPinned
                useLunar = event.useLunar
                isRepeatYearly = event.isRepeatYearly
                note = event.note
                remindBefore = event.remindBefore
                isEditing = true
                currentStep = 1
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑倒数日" else "添加倒数日") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 0 && !isEditing) currentStep-- else onBack()
                    }) { Icon(Icons.Filled.ArrowBack, "返回") }
                },
                actions = {
                    if (currentStep == 1) {
                        TextButton(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val colorHex = "#${Integer.toHexString(selectedColor.toArgb()).substring(2)}"
                                    val event = CountdownEvent(
                                        id = if (isEditing) eventId else 0,
                                        title = title.trim(),
                                        targetDate = selectedDate,
                                        isCountdown = isCountdown,
                                        category = selectedCategory,
                                        bgColor = colorHex,
                                        isPinned = isPinned,
                                        useLunar = useLunar,
                                        isRepeatYearly = isRepeatYearly,
                                        note = note.trim(),
                                        remindBefore = remindBefore
                                    )
                                    if (isEditing) viewModel.updateEvent(event) else viewModel.addEvent(event)

                                    if (remindBefore > 0) {
                                        val effectiveDate = DateUtils.getEffectiveTargetDate(selectedDate, isRepeatYearly)
                                        val remindTime = ReminderManager.calcRemindTime(effectiveDate, remindBefore)
                                        if (remindTime > System.currentTimeMillis()) {
                                            ReminderManager.scheduleReminder(
                                                context = com.timecalendar.app.TimeCalendarApp.instance,
                                                eventId = event.id,
                                                title = "时光历提醒",
                                                message = "「$title」还有 $remindBefore 天",
                                                triggerMillis = remindTime
                                            )
                                        }
                                    }
                                    onSaved()
                                }
                            },
                            enabled = title.isNotBlank()
                        ) { Text("保存", fontWeight = FontWeight.Bold) }
                    }
                }
            )
        }
    ) { padding ->
        when (currentStep) {
            0 -> TemplateSelectionScreen(
                modifier = Modifier.padding(padding),
                onTemplateSelected = { template ->
                    title = template.title
                    selectedCategory = template.category
                    isRepeatYearly = template.isRepeatYearly
                    useLunar = template.useLunar
                    isCountdown = template.isCountdown
                    selectedColor = try { Color(android.graphics.Color.parseColor(template.bgColor)) } catch (_: Exception) { Color(0xFFFF6B9D) }
                    currentStep = 1
                },
                onBlankSelected = { currentStep = 1 }
            )

            1 -> DetailSettingsScreen(
                modifier = Modifier.padding(padding),
                title = title, onTitleChange = { title = it },
                selectedDate = selectedDate, onDateChange = { selectedDate = it },
                isCountdown = isCountdown, onModeChange = { isCountdown = it },
                selectedCategory = selectedCategory, onCategoryChange = { selectedCategory = it },
                allCategories = allCategories,
                selectedColor = selectedColor, onColorChange = { selectedColor = it },
                isPinned = isPinned, onPinnedChange = { isPinned = it },
                useLunar = useLunar, onLunarChange = { useLunar = it },
                isRepeatYearly = isRepeatYearly, onRepeatChange = { isRepeatYearly = it },
                note = note, onNoteChange = { note = it },
                showWheelDatePicker = showWheelDatePicker, onShowWheelDatePickerChange = { showWheelDatePicker = it },
                remindBefore = remindBefore, onRemindBeforeChange = { remindBefore = it }
            )
        }
    }
}

@Composable
private fun TemplateSelectionScreen(
    modifier: Modifier,
    onTemplateSelected: (CountdownTemplate) -> Unit,
    onBlankSelected: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onBlankSelected() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("空白创建", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Text("常用模板", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp))
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(((CountdownTemplates.templates.size + 1) / 2 * 72).dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(CountdownTemplates.templates) { template ->
                    val color = try { Color(android.graphics.Color.parseColor(template.bgColor)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                    Card(
                        modifier = Modifier.fillMaxWidth().height(60.dp).clickable { onTemplateSelected(template) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(template.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (template.isRepeatYearly) Text("每年", fontSize = 9.sp, color = color.copy(alpha = 0.5f))
                                if (template.useLunar) Text("农历", fontSize = 9.sp, color = color.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSettingsScreen(
    modifier: Modifier,
    title: String, onTitleChange: (String) -> Unit,
    selectedDate: Long, onDateChange: (Long) -> Unit,
    isCountdown: Boolean, onModeChange: (Boolean) -> Unit,
    selectedCategory: String, onCategoryChange: (String) -> Unit,
    allCategories: List<String>,
    selectedColor: Color, onColorChange: (Color) -> Unit,
    isPinned: Boolean, onPinnedChange: (Boolean) -> Unit,
    useLunar: Boolean, onLunarChange: (Boolean) -> Unit,
    isRepeatYearly: Boolean, onRepeatChange: (Boolean) -> Unit,
    note: String, onNoteChange: (String) -> Unit,
    showWheelDatePicker: Boolean, onShowWheelDatePickerChange: (Boolean) -> Unit,
    remindBefore: Int, onRemindBeforeChange: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            OutlinedTextField(
                value = title, onValueChange = onTitleChange,
                label = { Text("事件名称") },
                placeholder = { Text("例如：生日、考试、旅行...") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Edit, null) }
            )
        }

        // 农历/公历 二选一
        item {
            Text("日期类型", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = !useLunar, onClick = { onLunarChange(false) },
                    label = { Text("公历") },
                    leadingIcon = if (!useLunar) {{ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = useLunar, onClick = { onLunarChange(true) },
                    label = { Text("农历") },
                    leadingIcon = if (useLunar) {{ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 日期选择
        item {
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onShowWheelDatePickerChange(true) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(if (useLunar) "农历日期" else "公历日期", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            DateUtils.formatDisplay(selectedDate),
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                        )
                        if (useLunar) {
                            val lunar = try {
                                com.timecalendar.app.util.LunarCalendar.getLunarDateString(
                                    DateUtils.getYear(selectedDate), DateUtils.getMonth(selectedDate), DateUtils.getDay(selectedDate)
                                )
                            } catch (_: Exception) { "" }
                            if (lunar.isNotBlank()) {
                                Text(lunar, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Icon(Icons.Filled.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // 倒数/正数
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(selected = isCountdown, onClick = { onModeChange(true) }, label = { Text("倒数") },
                    leadingIcon = if (isCountdown) {{ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f))
                FilterChip(selected = !isCountdown, onClick = { onModeChange(false) }, label = { Text("正数") },
                    leadingIcon = if (!isCountdown) {{ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f))
            }
        }

        // 分类
        item {
            Text("分类", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allCategories) { category ->
                    val color = CategoryColors[category] ?: Color.Gray
                    FilterChip(
                        selected = selectedCategory == category, onClick = { onCategoryChange(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.15f), selectedLabelColor = color
                        )
                    )
                }
            }
        }

        // 颜色 - 直接光谱选色
        item {
            Text("颜色", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            ColorSpectrumPicker(
                initialColor = selectedColor,
                onColorSelected = { onColorChange(it) }
            )
        }

        // 选项
        item {
            Text("选项", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = isPinned, onClick = { onPinnedChange(!isPinned) }, label = { Text("置顶") },
                    leadingIcon = if (isPinned) {{ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f))
                FilterChip(selected = isRepeatYearly, onClick = { onRepeatChange(!isRepeatYearly) }, label = { Text("每年重复") },
                    leadingIcon = if (isRepeatYearly) {{ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f))
            }
        }

        // 提醒
        item {
            Text("提醒", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0 to "不提醒", 0 to "当天", 1 to "提前1天", 3 to "提前3天", 7 to "提前7天").forEachIndexed { index, (value, _) ->
                    val actualValue = when (index) { 0 -> 0; 1 -> 0; 2 -> 1; 3 -> 3; 4 -> 7; else -> 0 }
                    val label = when (index) { 0 -> "不提醒"; 1 -> "当天"; 2 -> "提前1天"; 3 -> "提前3天"; 4 -> "提前7天"; else -> "" }
                    FilterChip(
                        selected = remindBefore == actualValue,
                        onClick = { onRemindBeforeChange(actualValue) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // 备注
        item {
            OutlinedTextField(
                value = note, onValueChange = onNoteChange,
                label = { Text("备注") },
                placeholder = { Text("可选，添加备注信息...") },
                modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    // 滚轮日期选择
    if (showWheelDatePicker) {
        AlertDialog(
            onDismissRequest = { onShowWheelDatePickerChange(false) },
            title = { Text(if (useLunar) "选择农历日期" else "选择公历日期") },
            text = {
                WheelDatePicker(
                    initialYear = DateUtils.getYear(selectedDate),
                    initialMonth = DateUtils.getMonth(selectedDate),
                    initialDay = DateUtils.getDay(selectedDate),
                    useLunar = useLunar,
                    onDateSelected = { year, month, day ->
                        val cal = java.util.Calendar.getInstance().apply {
                            set(year, month - 1, day, 0, 0, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }
                        onDateChange(cal.timeInMillis)
                    }
                )
            },
            confirmButton = { TextButton(onClick = { onShowWheelDatePickerChange(false) }) { Text("确定") } },
            dismissButton = { TextButton(onClick = { onShowWheelDatePickerChange(false) }) { Text("取消") } }
        )
    }
}
