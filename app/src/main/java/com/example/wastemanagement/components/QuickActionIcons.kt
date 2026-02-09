package com.example.wastemanagement.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wastemanagement.datastore.ThemeViewModel

data class QuickAction(val icon: ImageVector, val title: String, val route: String, val darkIcon: ImageVector? = null)

@Composable
fun QuickActionIcons(navController: NavController, themeViewModel: ThemeViewModel = hiltViewModel()) {
    
    val themeMode by themeViewModel.themeMode.collectAsState()
    val isDark = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val actions = listOf(
        QuickAction(Icons.Default.History, "My Reports", "my_reports"),
        QuickAction(Icons.Default.LocationOn, "Bins Near Me", "smart_bins_map"),
        QuickAction(Icons.Default.BarChart, "Statistics", "statistics"),
        QuickAction(Icons.Default.Settings, "Settings", "settings"),
        QuickAction(Icons.Default.DarkMode, "Dark Mode", "", darkIcon = Icons.Default.LightMode) // Special action
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEach { action ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = {
                    if (action.route.isNotEmpty()) {
                        navController.navigate(action.route)
                    } else {
                        // Handle dark mode toggle
                        val newTheme = if(isDark) "Light" else "Dark"
                        themeViewModel.setTheme(newTheme)
                    }
                }) {
                    val icon = if (action.darkIcon != null && isDark) action.darkIcon else action.icon
                    Icon(icon, contentDescription = action.title, tint = MaterialTheme.colorScheme.primary)
                }
                val title = if(action.darkIcon != null) (if(isDark) "Light Mode" else "Dark Mode") else action.title
                Text(title, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
