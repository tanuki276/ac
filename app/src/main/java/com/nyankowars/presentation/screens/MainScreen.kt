package com.nyankowars.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nyankowars.presentation.screens.battle.BattleScreen
import com.nyankowars.presentation.screens.cats.CatCollectionScreen
import com.nyankowars.presentation.screens.home.HomeScreen
import com.nyankowars.presentation.screens.settings.SettingsScreen
import com.nyankowars.presentation.screens.shop.ShopScreen
import com.nyankowars.presentation.theme.NyankoWarsTheme
import com.nyankowars.presentation.viewmodels.PlayerViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "ホーム", Icons.Default.Home)
    object Cats : Screen("cats", "にゃんこ図鑑", Icons.Default.Pets)
    object Battle : Screen("battle", "バトル", Icons.Default.Battle)
    object Shop : Screen("shop", "ショップ", Icons.Default.ShoppingCart)
    object Settings : Screen("settings", "設定", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    NyankoWarsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
            
            val bottomNavItems = listOf(
                Screen.Home,
                Screen.Cats,
                Screen.Battle,
                Screen.Shop,
                Screen.Settings
            )
            
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            // TODO: バッジ表示（例: 新しいクエストがあるなど）
                                        }
                                    ) {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title
                                        )
                                    }
                                },
                                label = { Text(screen.title) },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                playerViewModel = playerViewModel,
                                navController = navController
                            )
                        }
                        composable(Screen.Cats.route) {
                            CatCollectionScreen(
                                playerViewModel = playerViewModel
                            )
                        }
                        composable(Screen.Battle.route) {
                            BattleScreen(
                                playerViewModel = playerViewModel
                            )
                        }
                        composable(Screen.Shop.route) {
                            ShopScreen()
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                playerViewModel = playerViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}