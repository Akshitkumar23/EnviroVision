package com.example.wastemanagement.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.screens.viewmodel.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeScreenViewModel) {

    val incidents by homeViewModel.filteredIncidents.collectAsState()
    val totalIncidents by homeViewModel.totalIncidents.collectAsState()
    val openIssues by homeViewModel.openIssues.collectAsState()
    val resolvedIncidents by homeViewModel.resolvedIncidents.collectAsState()
    val totalBins by homeViewModel.totalBins.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EnviroVision") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("admin_dashboard") }) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Dashboard")
                    }
                    IconButton(onClick = { /* TODO: Handle notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    NavigationItem(navController, "my_reports", Icons.Default.History, "My Reports")
                    NavigationItem(navController, "smart_bins_map", Icons.Default.LocationOn, "Smart Bins")
                    NavigationItem(navController, "settings", Icons.Default.Settings, "Settings")
                    NavigationItem(navController, "logout", Icons.Default.Logout, "Logout")
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navController.navigate("report_incident") },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(Icons.Filled.Add, "Report Incident", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        CitizenDashboard(
            paddingValues = paddingValues,
            navController = navController,
            incidents = incidents,
            totalIncidents = totalIncidents,
            openIssues = openIssues,
            resolvedIncidents = resolvedIncidents,
            totalBins = totalBins,
            selectedCategory = selectedCategory,
            onCategorySelect = { homeViewModel.onCategorySelected(it) }
        )
    }
}

@Composable
fun RowScope.NavigationItem(navController: NavController, route: String, icon: ImageVector, label: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { navController.navigate(route) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label)
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun CitizenDashboard(
    paddingValues: PaddingValues,
    navController: NavController,
    incidents: List<Incident>,
    totalIncidents: Int,
    openIssues: Int,
    resolvedIncidents: Int,
    totalBins: Int,
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        WelcomeMessage()
        Spacer(Modifier.height(24.dp))
        Text("Overall Statistics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        KeyMetricCards(totalIncidents, openIssues, resolvedIncidents, totalBins)
        Spacer(Modifier.height(24.dp))
        Text("Your Contribution", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        YourContributionStats(incidents)
        Spacer(Modifier.height(24.dp))
        QuickActionsRow(navController = navController)
        Spacer(Modifier.height(24.dp))
        IncidentCategoryFilter(selectedCategory, onCategorySelect)
        Spacer(Modifier.height(16.dp))
        RecentIncidentsList(incidents) { incidentId ->
            navController.navigate("incident_detail/$incidentId")
        }
    }
}

@Composable
fun WelcomeMessage() {
    Text(
        text = "Welcome, Citizen!",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun KeyMetricCards(total: Int, open: Int, resolved: Int, bins: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard(Modifier.weight(1f), Icons.Default.List, total.toString(), "Total Reports")
        MetricCard(Modifier.weight(1f), Icons.Default.Warning, open.toString(), "Open Reports")
        MetricCard(Modifier.weight(1f), Icons.Default.CheckCircle, resolved.toString(), "Resolved")
        MetricCard(Modifier.weight(1f), Icons.Default.LocationOn, bins.toString(), "Smart Bins")
    }
}

@Composable
fun YourContributionStats(incidents: List<Incident>) {
    val myIncidents = incidents.filter { it.reportedBy == "user_id" } // Assuming a placeholder user_id
    val totalMyReports = myIncidents.size
    val resolvedMyReports = myIncidents.count { it.status.equals("Resolved", ignoreCase = true) }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard(Modifier.weight(1f), Icons.Default.Outbox, totalMyReports.toString(), "Reports Filed")
        MetricCard(Modifier.weight(1f), Icons.Default.Verified, resolvedMyReports.toString(), "Reports Resolved")
    }
}

@Composable
fun MetricCard(modifier: Modifier, icon: ImageVector, value: String, label: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, label, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(label, fontSize = 12.sp, color = Color.Gray, maxLines = 2, minLines = 2)
        }
    }
}

@Composable
fun QuickActionsRow(navController: NavController) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            QuickActionCard(icon = Icons.Default.History, label = "My Reports") {
                navController.navigate("my_reports")
            }
        }
        item {
            QuickActionCard(icon = Icons.Default.LocationOn, label = "Smart Bins") {
                navController.navigate("smart_bins_map")
            }
        }
        item {
            QuickActionCard(icon = Icons.Default.Settings, label = "Settings") {
                navController.navigate("settings")
            }
        }
        item {
            QuickActionCard(icon = Icons.Default.Add, label = "Smart Report") {
                navController.navigate("smart_report")
            }
        }
    }
}

@Composable
fun QuickActionCard(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentCategoryFilter(selected: String?, onSelect: (String) -> Unit) {
    val categories = listOf("All", "Illegal Dumping", "Overflowing Bin", "Damaged Bin", "Littering", "Hazardous Waste", "Other")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        items(categories) { category ->
            FilterChip(
                selected = category == selected,
                onClick = { onSelect(category) },
                label = { Text(category) },
                leadingIcon = { if (category == selected) Icon(Icons.Default.Check, "Selected") else null },
                shape = CircleShape
            )
        }
    }
}

@Composable
fun RecentIncidentsList(incidents: List<Incident>, onViewIncident: (String) -> Unit) {
    Column {
        Text("Recent Incidents", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        if (incidents.isEmpty()) {
            Text("No incidents match the selected filter.", modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                incidents.take(5).forEach { incident ->
                    IncidentListItem(incident = incident) { onViewIncident(incident.id) }
                }
            }
        }
    }
}

@Composable
fun IncidentListItem(incident: Incident, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Category, incident.type, modifier = Modifier.size(40.dp).padding(end = 12.dp), tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(incident.description, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(incident.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(Modifier.width(8.dp))
            SeverityTag(incident.severity)
        }
    }
}

@Composable
fun SeverityTag(severity: String) {
    val (bgColor, textColor) = when (severity.lowercase()) {
        "high" -> MaterialTheme.colorScheme.error to Color.White
        "medium" -> MaterialTheme.colorScheme.secondary to Color.Black
        "low" -> MaterialTheme.colorScheme.primary to Color.White
        else -> Color.LightGray to Color.Black
    }
    Card(shape = CircleShape, colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Text(
            text = severity,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
