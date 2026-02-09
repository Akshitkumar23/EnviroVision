package com.example.wastemanagement

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wastemanagement.components.AdminDrawer
import com.example.wastemanagement.components.AppDrawer
import com.example.wastemanagement.datastore.ThemeViewModel
import com.example.wastemanagement.navigation.AppNavigation
import com.example.wastemanagement.ui.theme.WasteManagementTheme
import kotlinx.coroutines.launch

@Composable
fun WasteManagementApp() {
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val themeMode by themeViewModel.themeMode.collectAsState()
    val isDark = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> false // Defaulting to light theme for simplicity
    }

    WasteManagementTheme(darkTheme = isDark) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val closeDrawer: () -> Unit = { scope.launch { drawerState.close() } }
        val onItemClick: (String) -> Unit = {
            closeDrawer()
            navController.navigate(it)
        }

        // Conditionally select the drawer content based on the current route
        val drawerContent: @Composable () -> Unit = if (currentRoute == "admin_dashboard") {
            { AdminDrawer(onClose = closeDrawer, onItemClick = onItemClick) }
        } else {
            { AppDrawer(onClose = closeDrawer, onItemClick = onItemClick) }
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = drawerContent,
            gesturesEnabled = false // Disable swipe gestures
        ) {
            AppNavigation(
                navController = navController,
                onMenuClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}
