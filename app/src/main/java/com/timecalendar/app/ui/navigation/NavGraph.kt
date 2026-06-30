package com.timecalendar.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Countdown : Screen("countdown")
    data object Health : Screen("health")
    data object Profile : Screen("profile")
    data object AddCountdown : Screen("add_countdown/{eventId}") {
        fun createRoute(eventId: Long = -1L) = "add_countdown/$eventId"
    }
    data object AddPeriod : Screen("add_period/{recordId}") {
        fun createRoute(recordId: Long = -1L) = "add_period/$recordId"
    }
    data object DateCalculator : Screen("date_calculator")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "日历", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomNavItem(Screen.Countdown, "倒数日", Icons.Filled.Timer, Icons.Outlined.Timer),
    BottomNavItem(Screen.Health, "健康", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    BottomNavItem(Screen.Profile, "我的", Icons.Filled.Person, Icons.Outlined.Person)
)
