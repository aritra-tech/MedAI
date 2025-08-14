package com.aritradas.medai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val name: String,
    val screen: Screens,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            name = "Prescription",
            screen = Screens.Prescription,
            iconOutlined = Icons.AutoMirrored.Outlined.Assignment,
            iconFilled = Icons.AutoMirrored.Filled.Assignment
        ),
        BottomNavItem(
            name = "Reports",
            screen = Screens.MedicalReportScreen,
            iconOutlined = Icons.Outlined.MedicalInformation,
            iconFilled = Icons.Filled.MedicalInformation
        ),
        BottomNavItem(
            name = "Profile",
            screen = Screens.Profile,
            iconOutlined = Icons.Outlined.Person,
            iconFilled = Icons.Filled.Person
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentDestination?.hasRoute(item.screen::class) == true
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
                    navController.navigate(item.screen) {
                        popUpTo<Screens.Prescription> {
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