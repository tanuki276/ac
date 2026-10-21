package com.nyankowars.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.nyankowars.presentation.viewmodels.PlayerViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "ホーム", Icons.Default.Home)
    object Cats : Screen("cats", "にゃんこ", Icons.Default.Favorite)
    object Battle : Screen("battle", "バトル", Icons.Default.Star)
    object Shop : Screen("shop", "ショップ", Icons.Default.Person)
    object Settings : Screen("settings", "設定", Icons.Default.Settings)
}

@Composable
fun MainScreen(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val items = listOf(Screen.Home, Screen.Cats, Screen.Battle, Screen.Shop, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) { HomeScreen(playerViewModel, navController) }
                composable(Screen.Cats.route) { CatCollectionScreen(playerViewModel) }
                composable(Screen.Battle.route) { BattleScreen(playerViewModel) }
                composable(Screen.Shop.route) { ShopScreen() }
                composable(Screen.Settings.route) { SettingsScreen(playerViewModel) }
            }
        }
    }
}
