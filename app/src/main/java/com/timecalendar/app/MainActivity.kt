package com.timecalendar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.timecalendar.app.ui.navigation.*
import com.timecalendar.app.ui.screens.countdown.AddCountdownScreen
import com.timecalendar.app.ui.screens.countdown.CountdownScreen
import com.timecalendar.app.ui.screens.health.AddPeriodScreen
import com.timecalendar.app.ui.screens.health.HealthScreen
import com.timecalendar.app.ui.screens.home.HomeScreen
import com.timecalendar.app.ui.screens.profile.DateCalculatorScreen
import com.timecalendar.app.ui.screens.profile.ProfileScreen
import com.timecalendar.app.ui.theme.TimeCalendarTheme
import com.timecalendar.app.viewmodel.CountdownViewModel
import com.timecalendar.app.viewmodel.HealthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeCalendarTheme {
                TimeCalendarMainScreen()
            }
        }
    }
}

@Composable
fun TimeCalendarMainScreen() {
    val navController = rememberNavController()
    val countdownViewModel: CountdownViewModel = viewModel()
    val healthViewModel: HealthViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Screens that show bottom bar
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Countdown.route,
        Screen.Health.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(countdownViewModel = countdownViewModel)
            }

            composable(Screen.Countdown.route) {
                CountdownScreen(
                    viewModel = countdownViewModel,
                    onAddClick = {
                        navController.navigate(Screen.AddCountdown.createRoute())
                    },
                    onEventClick = { eventId ->
                        navController.navigate(Screen.AddCountdown.createRoute(eventId))
                    }
                )
            }

            composable(Screen.Health.route) {
                HealthScreen(
                    viewModel = healthViewModel,
                    onAddPeriod = {
                        navController.navigate(Screen.AddPeriod.createRoute())
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    countdownViewModel = countdownViewModel,
                    healthViewModel = healthViewModel,
                    onDateCalculator = {
                        navController.navigate(Screen.DateCalculator.route)
                    }
                )
            }

            composable(
                route = Screen.AddCountdown.route,
                arguments = listOf(navArgument("eventId") { type = NavType.LongType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getLong("eventId") ?: -1L
                AddCountdownScreen(
                    viewModel = countdownViewModel,
                    eventId = eventId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AddPeriod.route,
                arguments = listOf(navArgument("recordId") { type = NavType.LongType })
            ) { backStackEntry ->
                val recordId = backStackEntry.arguments?.getLong("recordId") ?: -1L
                AddPeriodScreen(
                    viewModel = healthViewModel,
                    recordId = recordId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Screen.DateCalculator.route) {
                DateCalculatorScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
