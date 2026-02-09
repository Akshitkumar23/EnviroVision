package com.example.wastemanagement.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wastemanagement.screens.CitizenDashboard
import com.example.wastemanagement.screens.auth.AuthRouter
import com.example.wastemanagement.screens.auth.RoleSelectionScreen
import com.example.wastemanagement.screens.admindetail.AdminIncidentDetailScreen
import com.example.wastemanagement.screens.details.IncidentDetailScreen
import com.example.wastemanagement.screens.events.EventsScreen
import com.example.wastemanagement.screens.feature.AdminDashboardScreen
import com.example.wastemanagement.screens.login.AuthViewModel
import com.example.wastemanagement.screens.login.LoginScreen
import com.example.wastemanagement.screens.map.SmartBinMapScreen
import com.example.wastemanagement.screens.myreports.MyReportsScreen
import com.example.wastemanagement.screens.plantation.CreateDriveScreen
import com.example.wastemanagement.screens.plantation.PlantationDrivesScreen
import com.example.wastemanagement.screens.report.ReportIncidentScreen
import com.example.wastemanagement.screens.settings.SettingsScreen
import com.example.wastemanagement.screens.statistics.StatisticsScreen
import com.example.wastemanagement.screens.theme.ThemeScreen
import com.example.wastemanagement.screens.users.UsersScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "auth_router") {
        composable("auth_router") { AuthRouter(navController) }
        composable("role_selection") { RoleSelectionScreen(navController) }
        composable("login/{role}") { backStackEntry ->
            LoginScreen(navController, backStackEntry.arguments?.getString("role"), authViewModel)
        }
        composable("citizen_dashboard") { CitizenDashboard(navController, onMenuClick) }
        composable("admin_dashboard") { AdminDashboardScreen(navController, onMenuClick) }
        composable("report_incident?incidentId={incidentId}") {
            ReportIncidentScreen(navController)
        }
        composable("incident_details/{incidentId}") { backStackEntry ->
            IncidentDetailScreen(navController)
        }
        composable("admin_incident_details/{incidentId}") { backStackEntry ->
            AdminIncidentDetailScreen(navController)
        }
        composable("my_reports") {
            MyReportsScreen(navController = navController)
        }
        composable("smart_bin_map") { SmartBinMapScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("events_screen") { EventsScreen(navController) }
        composable("statistics") { StatisticsScreen(navController) }
        composable("plantation_drives") { PlantationDrivesScreen(navController) }
        composable("create_drive") { CreateDriveScreen(navController) }
        composable("theme") { ThemeScreen(navController) }
        composable("users_screen") { UsersScreen(navController) }
        composable("logout") {
            authViewModel.signOut(context)
            navController.navigate("auth_router") {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }
}
