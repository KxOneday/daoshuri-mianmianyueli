package com.timecalendar.app.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timecalendar.app.viewmodel.CountdownViewModel
import com.timecalendar.app.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    countdownViewModel: CountdownViewModel,
    healthViewModel: HealthViewModel,
    onDateCalculator: () -> Unit
) {
    val eventCount by countdownViewModel.allEvents.collectAsState()
    val periodCount by healthViewModel.periodCount.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "时光历",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "记录时光流转的每一个日子",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("倒数日", "${eventCount.size}")
                        StatItem("经期记录", "$periodCount")
                    }
                }
            }
        }

        // Tools
        item {
            Text(
                "工具",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Calculate,
                title = "日期计算器",
                subtitle = "计算日期间隔或推算N天后",
                onClick = onDateCalculator
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Image,
                title = "海报分享",
                subtitle = "生成精美海报分享到社交平台",
                onClick = {}
            )
        }

        // Settings
        item {
            Text(
                "设置",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Palette,
                title = "主题",
                subtitle = "跟随系统",
                onClick = {}
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Lock,
                title = "隐私锁",
                subtitle = "保护你的隐私数据",
                onClick = {}
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.Outlined.CloudSync,
                title = "云端同步",
                subtitle = "数据多设备同步",
                onClick = {}
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Backup,
                title = "数据备份",
                subtitle = "本地备份与恢复",
                onClick = {}
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Widgets,
                title = "桌面组件",
                subtitle = "添加桌面小组件",
                onClick = {}
            )
        }

        // About
        item {
            Text(
                "关于",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        item {
            ProfileMenuItem(
                icon = Icons.Outlined.Info,
                title = "关于时光历",
                subtitle = "版本 1.0.0",
                onClick = {}
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
