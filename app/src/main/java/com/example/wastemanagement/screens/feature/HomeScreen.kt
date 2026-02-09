package com.example.wastemanagement.screens.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wastemanagement.screens.feature.components.*
import com.example.wastemanagement.screens.viewmodel.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeScreenViewModel = hiltViewModel()) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Waste Management") },
                actions = {
                    // Action to navigate to the admin dashboard
                    IconButton(onClick = { navController.navigate("admin_dashboard") }) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Switch to Admin View",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("report_incident") },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Report Incident", tint = Color.Black)
            }
        }
    ) { paddingValues ->
        // The content of the home screen is always the citizen dashboard
        CitizenDashboard(navController = navController, modifier = Modifier.padding(paddingValues))
    }
}

@Composable
fun CitizenDashboard(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        WelcomeMessage()
        KeyMetricCards()
        IncidentCategoryFilter()
        RecentIncidentsList(navController = navController)
        SmartBinsMapPreview(navController = navController)
        IncidentTrendChart()
        ActionButtons(navController = navController)
    }
}
