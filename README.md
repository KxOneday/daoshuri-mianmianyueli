# 时光历 (TimeCalendar)

> 记录时光流转的每一个日子

一款融合**倒数日**与**经期管理**的一站式日子记录 App。

## ✨ 功能特色

### 📅 统一日历
- 月历视图，一个界面展示所有事件
- 公历/农历双显（1901-2049）
- 多层标记：倒数日、经期、节日一目了然
- 点击日期查看当天所有关联事件

### ⏰ 倒数日
- 倒数/正数双模式
- 分类管理（工作/生活/健康/学习/节日）
- 自定义颜色、置顶、农历、每年重复
- 到期提醒通知

### 🩺 健康管理
- 经期日期记录（流量、痛经、心情、症状）
- 智能预测（基于历史数据，越用越准）
- 周期趋势分析（12个月数据对比）
- 排卵推算与备孕辅助
- 习惯打卡

### 🎨 个性化
- Material 3 设计 + 动态取色
- 多种主题色彩
- 自定义事件颜色
- 桌面小组件

### 🔐 安全
- 密码锁/指纹/面容识别
- 隐藏敏感事件
- 本地数据加密

### 🛠 工具
- 日期计算器（算间隔 / 推算N天后）
- 海报生成与分享

## 🏗 技术栈

- **语言：** Kotlin
- **UI：** Jetpack Compose + Material 3
- **架构：** MVVM
- **数据库：** Room
- **导航：** Navigation Compose
- **最低版本：** Android 8.0 (API 26)

## 📦 构建

```bash
# Debug
./gradlew assembleDebug

# Release
./gradlew assembleRelease
```

APK 输出路径：`app/build/outputs/apk/`

## 📁 项目结构

```
app/src/main/java/com/timecalendar/app/
├── data/
│   ├── local/          # Room 数据库、DAO、实体
│   └── repository/     # 数据仓库层
├── ui/
│   ├── theme/          # Material 3 主题
│   ├── navigation/     # 导航配置
│   └── screens/        # 各页面 Composable
├── viewmodel/          # ViewModel 层
├── widget/             # 桌面小组件
└── util/               # 工具类（农历、日期等）
```

## 📝 License

MIT
