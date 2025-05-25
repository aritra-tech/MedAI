package com.aritradas.medai.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aritradas.medai.navigation.Screens

data class BottomNavItem(
    val name: String,
    val route: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            name = "Prescription",
            route = Screens.Prescription.route,
            iconOutlined = Icons.AutoMirrored.Outlined.Assignment,
            iconFilled = Icons.AutoMirrored.Filled.Assignment
        ),
        BottomNavItem(
            name = "Profile",
            route = Screens.Profile.route,
            iconOutlined = Icons.Outlined.Person,
            iconFilled = Icons.Filled.Person
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.iconFilled else item.iconOutlined,
                        contentDescription = item.name
                    )
                },
                label = { Text(text = item.name) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
