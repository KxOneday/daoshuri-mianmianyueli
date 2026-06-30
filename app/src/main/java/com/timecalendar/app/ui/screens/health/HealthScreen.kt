package com.timecalendar.app.ui.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timecalendar.app.data.local.entity.PeriodRecord
import com.timecalendar.app.ui.theme.*
import com.timecalendar.app.util.DateUtils
import com.timecalendar.app.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(
    viewModel: HealthViewModel,
    onAddPeriod: () -> Unit
) {
    val recentPeriods by viewModel.recentPeriods.collectAsState()
    val averageCycle by viewModel.averageCycle.collectAsState()
    val averageDuration by viewModel.averageDuration.collectAsState()
    val nextPeriodStart by viewModel.nextPeriodStart.collectAsState()
    val currentCycleDay by viewModel.currentCycleDay.collectAsState()
    val cycleLengths by viewModel.cycleLengths.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPeriod,
                containerColor = PeriodRed
            ) {
                Icon(Icons.Filled.Add, "记录经期")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cycle Status Card
            item {
                CycleStatusCard(
                    currentCycleDay = currentCycleDay,
                    averageCycle = averageCycle,
                    nextPeriodStart = nextPeriodStart
                )
            }

            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "平均周期",
                        value = "${averageCycle ?: "--"}",
                        unit = "天",
                        color = PeriodRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "经期天数",
                        value = "${averageDuration ?: "--"}",
                        unit = "天",
                        color = OvulationGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "记录次数",
                        value = "${recentPeriods.size}",
                        unit = "次",
                        color = CountdownBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Cycle Trend
            if (cycleLengths.size >= 2) {
                item {
                    CycleTrendCard(cycleLengths)
                }
            }

            // Recent Records
            item {
                Text(
                    "近期记录",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (recentPeriods.isEmpty()) {
                item {
                    EmptyHealthState(onAddPeriod = onAddPeriod)
                }
            } else {
                items(recentPeriods) { record ->
                    PeriodRecordCard(
                        record = record,
                        onDelete = { viewModel.deletePeriod(record) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CycleStatusCard(
    currentCycleDay: Int?,
    averageCycle: Int?,
    nextPeriodStart: Long?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PeriodRed.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cycle ring indicator
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = (currentCycleDay?.toFloat() ?: 0f) / (averageCycle?.toFloat() ?: 28f),
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = PeriodRed,
                    trackColor = PeriodRed.copy(alpha = 0.1f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "第 ${currentCycleDay ?: "--"} 天",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PeriodRed
                    )
                    Text(
                        text = "周期 ${averageCycle ?: 28} 天",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (nextPeriodStart != null) {
                val daysUntil = DateUtils.getDaysFromNow(nextPeriodStart)
                Text(
                    text = if (daysUntil > 0) "距离下次经期还有 $daysUntil 天"
                    else if (daysUntil == 0) "今天可能是经期开始日"
                    else "经期可能已开始 ${-daysUntil} 天",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
                Text(unit, fontSize = 12.sp, color = color.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
            }
        }
    }
}

@Composable
private fun CycleTrendCard(cycleLengths: List<Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("周期趋势", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            // Simple bar chart
            val maxLen = cycleLengths.maxOrNull() ?: 28
            val minLen = cycleLengths.minOrNull() ?: 28
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                cycleLengths.takeLast(8).forEach { length ->
                    val height = ((length - minLen + 5).toFloat() / (maxLen - minLen + 10)) * 80 + 20
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text("$length", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(height.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(PeriodRed.copy(alpha = 0.6f + (length.toFloat() / maxLen) * 0.4f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "最近 ${cycleLengths.size} 个周期",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PeriodRecordCard(record: PeriodRecord, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条经期记录吗？") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("取消") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PeriodRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${DateUtils.getDay(record.startDate)}",
                        fontWeight = FontWeight.Bold,
                        color = PeriodRed
                    )
                    Text(
                        "${DateUtils.getMonth(record.startDate)}月",
                        fontSize = 9.sp,
                        color = PeriodRed.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    DateUtils.formatDisplay(record.startDate),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (record.endDate != null) {
                    val duration = ((record.endDate - record.startDate) / (1000 * 60 * 60 * 24)).toInt() + 1
                    Text(
                        "持续 $duration 天 · 流量${when(record.flow) { 1 -> "少" 2 -> "中" 3 -> "多" else -> "中" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (record.mood.isNotBlank()) {
                    Text(
                        "心情：${record.mood}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Filled.Delete,
                    "删除",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun EmptyHealthState(onAddPeriod: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = PeriodRed.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("还没有经期记录", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "记录经期，智能预测下一次时间",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddPeriod, colors = ButtonDefaults.buttonColors(containerColor = PeriodRed)) {
            Text("开始记录")
        }
    }
}
