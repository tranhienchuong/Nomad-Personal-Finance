package com.tranhienchuong.nomad.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tranhienchuong.nomad.core.designsystem.NomadTheme
import com.tranhienchuong.nomad.feature.auth.AuthScreen
import com.tranhienchuong.nomad.feature.auth.OnboardingScreen
import com.tranhienchuong.nomad.feature.budget.BudgetScreen
import com.tranhienchuong.nomad.feature.home.HomeScreen
import com.tranhienchuong.nomad.feature.profile.ProfileScreen
import com.tranhienchuong.nomad.feature.statistics.StatisticsScreen
import com.tranhienchuong.nomad.feature.transaction.TransactionScreen
import com.tranhienchuong.nomad.ui.splash.SplashScreen

private object RootRoute {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Auth = "auth"
    const val Main = "main"
}

private enum class MainDestination(val route: String, val label: String) {
    Home("home", "Home"),
    Transactions("transactions", "Transactions"),
    Budget("budget", "Budget"),
    Statistics("statistics", "Statistics"),
    Profile("profile", "Profile"),
}

@Composable
fun NomadApp() {
    val rootNavController = rememberNavController()

    NomadTheme {
        NavHost(
            navController = rootNavController,
            startDestination = RootRoute.Splash,
        ) {
            composable(RootRoute.Splash) {
                SplashScreen(
                    onNavigateToMain = {
                        rootNavController.navigate(RootRoute.Main) {
                            popUpTo(RootRoute.Splash) { inclusive = true }
                        }
                    },
                    onNavigateToAuth = {
                        rootNavController.navigate(RootRoute.Auth) {
                            popUpTo(RootRoute.Splash) { inclusive = true }
                        }
                    },
                    onNavigateToOnboarding = {
                        rootNavController.navigate(RootRoute.Onboarding) {
                            popUpTo(RootRoute.Splash) { inclusive = true }
                        }
                    },
                )
            }
            composable(RootRoute.Onboarding) {
                OnboardingScreen(
                    onFinished = {
                        rootNavController.navigate(RootRoute.Auth) {
                            popUpTo(RootRoute.Onboarding) { inclusive = true }
                        }
                    },
                )
            }
            composable(RootRoute.Auth) {
                AuthScreen(
                    onAuthSuccess = {
                        rootNavController.navigate(RootRoute.Main) {
                            popUpTo(RootRoute.Auth) { inclusive = true }
                        }
                    },
                )
            }
            composable(RootRoute.Main) { MainNavigation() }
        }
    }
}

@Composable
private fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(destination.label.first().toString()) },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainDestination.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(MainDestination.Home.route) { HomeScreen() }
            composable(MainDestination.Transactions.route) { TransactionScreen() }
            composable(MainDestination.Budget.route) { BudgetScreen() }
            composable(MainDestination.Statistics.route) { StatisticsScreen() }
            composable(MainDestination.Profile.route) { ProfileScreen() }
        }
    }
}
