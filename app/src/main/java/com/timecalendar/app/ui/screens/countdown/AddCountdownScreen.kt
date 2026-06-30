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
import com.timecalendar.app.util.SystemCalendarHelper
import com.timecalendar.app.viewmodel.CountdownViewModel

@Composable
fun AddCountdownScreen(
    viewModel: CountdownViewModel,
    eventId: Long = -1L,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) } // 0=模板选择, 1=详细设置
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
    var showColorPicker by remember { mutableStateOf(false) }
    var remindBefore by remember { mutableIntStateOf(0) } // 0=不提醒, 1=1天前, 3=3天前, 7=7天前
    var syncToSystemCalendar by remember { mutableStateOf(false) }

    val allCategories = listOf("生日", "恋爱", "学习", "工作", "生活", "健康", "节日", "还款", "其他")

    // Load existing event if editing
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
                    }) {
                        Icon(Icons.Filled.ArrowBack, "返回")
                    }
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
                                        note = note.trim()
                                    )
                                    if (isEditing) viewModel.updateEvent(event) else viewModel.addEvent(event)

                                    // Schedule reminder
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

                                    // Sync to system calendar
                                    if (syncToSystemCalendar) {
                                        SystemCalendarHelper.addEventToSystemCalendar(
                                            context = com.timecalendar.app.TimeCalendarApp.instance,
                                            title = title.trim(),
                                            startMillis = selectedDate
                                        )
                                    }

                                    onSaved()
                                }
                            },
                            enabled = title.isNotBlank()
                        ) {
                            Text("保存", fontWeight = FontWeight.Bold)
                        }
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
                    note = template.note
                    selectedColor = try {
                        Color(android.graphics.Color.parseColor(template.bgColor))
                    } catch (_: Exception) { Color(0xFFFF6B9D) }
                    currentStep = 1
                },
                onBlankSelected = {
                    currentStep = 1
                }
            )

            1 -> DetailSettingsScreen(
                modifier = Modifier.padding(padding),
                title = title,
                onTitleChange = { title = it },
                selectedDate = selectedDate,
                onDateChange = { selectedDate = it },
                isCountdown = isCountdown,
                onModeChange = { isCountdown = it },
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                allCategories = allCategories,
                selectedColor = selectedColor,
                onColorChange = { selectedColor = it },
                showColorPicker = showColorPicker,
                onShowColorPickerChange = { showColorPicker = it },
                isPinned = isPinned,
                onPinnedChange = { isPinned = it },
                useLunar = useLunar,
                onLunarChange = { useLunar = it },
                isRepeatYearly = isRepeatYearly,
                onRepeatChange = { isRepeatYearly = it },
                note = note,
                onNoteChange = { note = it },
                showWheelDatePicker = showWheelDatePicker,
                onShowWheelDatePickerChange = { showWheelDatePicker = it },
                remindBefore = remindBefore,
                onRemindBeforeChange = { remindBefore = it },
                syncToSystemCalendar = syncToSystemCalendar,
                onSyncChange = { syncToSystemCalendar = it }
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
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categories = CountdownTemplates.categories

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Blank option
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBlankSelected() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("空白创建", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Category filter
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("全部") }
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }
        }

        // Template grid
        val filtered = if (selectedCategory != null) {
            CountdownTemplates.getByCategory(selectedCategory!!)
        } else {
            CountdownTemplates.templates
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(((filtered.size + 1) / 2 * 100).dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(filtered) { template ->
                    TemplateCard(
                        template = template,
                        onClick = { onTemplateSelected(template) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(template: CountdownTemplate, onClick: () -> Unit) {
    val color = try {
        Color(android.graphics.Color.parseColor(template.bgColor))
    } catch (_: Exception) { MaterialTheme.colorScheme.primary }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                template.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (template.isRepeatYearly) {
                    Text("每年", fontSize = 10.sp, color = color.copy(alpha = 0.6f))
                }
                if (template.useLunar) {
                    Text("农历", fontSize = 10.sp, color = color.copy(alpha = 0.6f))
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
    showColorPicker: Boolean, onShowColorPickerChange: (Boolean) -> Unit,
    isPinned: Boolean, onPinnedChange: (Boolean) -> Unit,
    useLunar: Boolean, onLunarChange: (Boolean) -> Unit,
    isRepeatYearly: Boolean, onRepeatChange: (Boolean) -> Unit,
    note: String, onNoteChange: (String) -> Unit,
    showWheelDatePicker: Boolean, onShowWheelDatePickerChange: (Boolean) -> Unit,
    remindBefore: Int, onRemindBeforeChange: (Int) -> Unit,
    syncToSystemCalendar: Boolean, onSyncChange: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        item {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("事件名称") },
                placeholder = { Text("例如：生日、考试、旅行...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Edit, null) }
            )
        }

        // Date - wheel picker trigger
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowWheelDatePickerChange(true) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                        if (useLunar) {
                            val lunar = try {
                                com.timecalendar.app.util.LunarCalendar.getLunarDateString(
                                    DateUtils.getYear(selectedDate),
                                    DateUtils.getMonth(selectedDate),
                                    DateUtils.getDay(selectedDate)
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

        // Mode toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = isCountdown,
                    onClick = { onModeChange(true) },
                    label = { Text("倒数") },
                    leadingIcon = if (isCountdown) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = !isCountdown,
                    onClick = { onModeChange(false) },
                    label = { Text("正数") },
                    leadingIcon = if (!isCountdown) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category
        item {
            Text("分类", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allCategories) { category ->
                    val color = CategoryColors[category] ?: Color.Gray
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.15f),
                            selectedLabelColor = color
                        )
                    )
                }
            }
        }

        // Color - spectrum picker
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("颜色", style = MaterialTheme.typography.labelMedium)
                TextButton(onClick = { onShowColorPickerChange(!showColorPicker) }) {
                    Text(if (showColorPicker) "收起" else "自定义颜色")
                }
            }

            if (showColorPicker) {
                ColorSpectrumPicker(
                    initialColor = selectedColor,
                    onColorSelected = { onColorChange(it) }
                )
            } else {
                // Quick color grid
                val presetColors = listOf(
                    Color(0xFFFF6B9D), Color(0xFFE91E63), Color(0xFFF44336), Color(0xFFFF5722),
                    Color(0xFFFF9800), Color(0xFFFFC107), Color(0xFF4CAF50), Color(0xFF009688),
                    Color(0xFF00BCD4), Color(0xFF2196F3), Color(0xFF3F51B5), Color(0xFF9C27B0),
                    Color(0xFF607D8B), Color(0xFF795548), Color(0xFFE040FB), Color(0xFF000000)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier.height(60.dp),
                    userScrollEnabled = false
                ) {
                    items(presetColors) { color ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (selectedColor == color) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                                .clickable { onColorChange(color) }
                        )
                    }
                }
            }
        }

        // Options
        item {
            Text("选项", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isPinned,
                    onClick = { onPinnedChange(!isPinned) },
                    label = { Text("置顶") },
                    leadingIcon = if (isPinned) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = useLunar,
                    onClick = { onLunarChange(!useLunar) },
                    label = { Text("农历") },
                    leadingIcon = if (useLunar) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = isRepeatYearly,
                    onClick = { onRepeatChange(!isRepeatYearly) },
                    label = { Text("每年重复") },
                    leadingIcon = if (isRepeatYearly) {{ Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }} else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Reminder
        item {
            Text("提醒", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0 to "不提醒", 1 to "当天", 3 to "3天前", 7 to "7天前").forEach { (value, label) ->
                    FilterChip(
                        selected = remindBefore == value,
                        onClick = { onRemindBeforeChange(value) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Sync to system calendar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("同步到系统日历", style = MaterialTheme.typography.bodyMedium)
                    Text("在手机日历中显示此事件", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = syncToSystemCalendar,
                    onCheckedChange = { onSyncChange(it) }
                )
            }
        }

        // Note
        item {
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("备注") },
                placeholder = { Text("可选，添加备注信息...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    // Wheel Date Picker Dialog
    if (showWheelDatePicker) {
        AlertDialog(
            onDismissRequest = { onShowWheelDatePickerChange(false) },
            title = { Text("选择日期") },
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
            confirmButton = {
                TextButton(onClick = { onShowWheelDatePickerChange(false) }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowWheelDatePickerChange(false) }) {
                    Text("取消")
                }
            }
        )
    }
}
