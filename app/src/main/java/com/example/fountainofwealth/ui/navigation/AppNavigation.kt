package com.example.fountainofwealth.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fountainofwealth.ui.screens.CryptoScreen
import com.example.fountainofwealth.ui.screens.HelpScreen
import com.example.fountainofwealth.ui.screens.HomeScreen
import com.example.fountainofwealth.ui.screens.LoginScreen
import com.example.fountainofwealth.ui.screens.RatesScreen
import com.example.fountainofwealth.ui.screens.StatsScreen
import com.example.fountainofwealth.viewmodel.MainViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.fountainofwealth.notification.ExpenseReminderNotifier

sealed class Screen(
    val route: String,
    val label: String
) {
    data object Login : Screen("login", "Login")
    data object Home : Screen("home", "Home")
    data object Rates : Screen("rates", "Rates")
    data object Crypto : Screen("crypto", "Crypto")
    data object Stats : Screen("stats", "MyStats")
    data object Help : Screen("help", "Help")
}

private val bottomScreens = listOf(
    Screen.Home,
    Screen.Rates,
    Screen.Crypto,
    Screen.Stats,
    Screen.Help
)

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val context = LocalContext.current

    val startDestination = if (uiState.isLoggedIn) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                uiState = uiState,
                onLogin = { email, password ->
                    viewModel.login(
                        email = email,
                        password = password,
                        onSuccess = {
                            ExpenseReminderNotifier.showLoginReminder(context)

                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                },
                onRegister = { email, password ->
                    viewModel.register(
                        email = email,
                        password = password,
                        onSuccess = {
                            ExpenseReminderNotifier.showLoginReminder(context)

                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                },
                onClearError = viewModel::clearError
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                uiState = uiState,
                onAddExpense = viewModel::addExpense,
                onDeleteExpense = viewModel::deleteExpense,
                onUpdateBudget = viewModel::updateBudget,
                onRefresh = viewModel::refreshAll,
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                bottomBar = { BottomNavigationBar(navController) }
            )
        }

        composable(Screen.Rates.route) {
            RatesScreen(
                uiState = uiState,
                onRefresh = viewModel::refreshAll,
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            )
        }

        composable(Screen.Crypto.route) {
            CryptoScreen(
                uiState = uiState,
                onRefresh = viewModel::refreshAll,
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                uiState = uiState,
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            )
        }

        composable(Screen.Help.route) {
            HelpScreen(
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: androidx.navigation.NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomScreens.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any {
                    it.route == screen.route
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Home -> Icons.Default.Home
                            Screen.Rates -> Icons.Default.CurrencyExchange
                            Screen.Crypto -> Icons.Default.Paid
                            Screen.Stats -> Icons.Default.BarChart
                            Screen.Help -> Icons.Default.Help
                            else -> Icons.Default.Home
                        },
                        contentDescription = screen.label
                    )
                },
                label = {
                    Text(screen.label)
                }
            )
        }
    }
}