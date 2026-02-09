package com.example.wastemanagement.screens.feature

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wastemanagement.components.ResolveIncidentDialog
import com.example.wastemanagement.components.getStatusColor
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.screens.login.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val filteredIncidents by viewModel.filteredIncidents.collectAsState()
    val user by authViewModel.user.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val dateRange by viewModel.dateRange.collectAsState()

    var showDateRangePicker by remember { mutableStateOf(false) }
    var incidentToResolve by remember { mutableStateOf<Incident?>(null) }
    var isFilterPanelVisible by remember { mutableStateOf(false) }

    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = dateRange?.first,
        initialSelectedEndDateMillis = dateRange?.second
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Menu") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D1B2A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        if (stats == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "header") { WelcomeAdminHeader(name = user?.displayName) }

                item(key = "overview_header") { SectionHeader("Overview") }
                item(key = "metric_cards_1") { 
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        stats?.let {
                            MetricCard("Pending", it.pendingIncidents.toString(), Icons.Default.PendingActions, Modifier.weight(1f), Color(0xFFE63946))
                            MetricCard("Resolved", it.resolvedIncidents.toString(), Icons.Default.TaskAlt, Modifier.weight(1f), Color(0xFF2A9D8F))
                        }
                    }
                }
                item(key = "metric_cards_2") { 
                     Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        stats?.let {
                            MetricCard("Total Reports", it.totalIncidents.toString(), Icons.Default.List, Modifier.weight(1f), Color(0xFF457B9D))
                            MetricCard("Total Users", it.totalUsers.toString(), Icons.Default.Group, Modifier.weight(1f), Color(0xFF1D3557))
                        }
                    }
                }

                item(key = "quick_actions_header") { SectionHeader("Quick Actions") }
                item(key = "qa_users") { QuickActionCard("User Management", "Manage roles and permissions", Icons.Default.Group, Color(0xFFE9F5DB)) { navController.navigate("users_screen") } }
                item(key = "qa_analytics") { QuickActionCard("Analytics & Reports", "View detailed statistics", Icons.Default.Assessment, Color(0xFFCDE2E5)) { navController.navigate("statistics") } }

                item(key = "manage_header") { SectionHeader("Manage Incidents") }
                item(key = "search_bar") {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search...") },
                            leadingIcon = { Icon(Icons.Default.Search, null) }
                        )
                        IconButton(onClick = { isFilterPanelVisible = !isFilterPanelVisible }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Show Filters")
                        }
                    }
                }
                
                item(key = "filter_panel") {
                     AnimatedVisibility(visible = isFilterPanelVisible) {
                        FilterControls(
                            statusFilter = statusFilter,
                            sortBy = sortBy,
                            onStatusSelected = viewModel::onStatusFilterChanged,
                            onSortSelected = viewModel::onSortChanged,
                            onDateFilterClicked = { showDateRangePicker = true }
                        )
                    }
                }

                item(key = "active_filters") { 
                    ActiveFiltersRow(
                        dateRange = dateRange,
                        onClearDateFilter = { viewModel.onDateRangeSelected(null) }
                    ) 
                }
                
                if (filteredIncidents.isEmpty()) {
                     item(key = "empty_state") {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No incidents match your criteria.", 
                                style = MaterialTheme.typography.bodyMedium, 
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                } else {
                    items(filteredIncidents, key = { it.id }) { incident ->
                        RecentIncidentCard(
                            incident = incident, 
                            onCardClick = { navController.navigate("admin_incident_details/${incident.id}") },
                            onStatusChange = { newStatus ->
                                if (newStatus == "Resolved") {
                                    incidentToResolve = incident
                                } else {
                                    viewModel.updateIncidentStatus(incident.id, newStatus)
                                }
                            }
                        )
                    }
                }
            }
        }

        if (incidentToResolve != null) {
            ResolveIncidentDialog(
                onDismiss = { incidentToResolve = null },
                onSubmit = { afterImageUri, comment ->
                    viewModel.updateIncidentStatus(incidentToResolve!!.id, "Resolved", afterImageUri, comment)
                    incidentToResolve = null
                }
            )
        }

        if (showDateRangePicker) {
             DatePickerDialog(
                onDismissRequest = { showDateRangePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            showDateRangePicker = false
                            datePickerState.selectedEndDateMillis?.let { 
                                val range = datePickerState.selectedStartDateMillis!! to it
                                viewModel.onDateRangeSelected(range)
                            }
                        },
                        enabled = datePickerState.selectedEndDateMillis != null,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Row {
                         TextButton(
                            onClick = { 
                                showDateRangePicker = false
                                viewModel.onDateRangeSelected(null)
                                datePickerState.setSelection(null, null)
                            },
                             colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Clear")
                        }
                        TextButton(
                            onClick = { 
                                showDateRangePicker = false 
                            },
                             colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            ) {
                DateRangePicker(
                    state = datePickerState, 
                    title = { Text(text = "Filter by Date Range", modifier = Modifier.padding(16.dp)) },
                    colors = DatePickerDefaults.colors(
                        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                        todayContentColor = MaterialTheme.colorScheme.primary,
                        todayDateBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterControls(
    statusFilter: String,
    sortBy: String,
    onStatusSelected: (String) -> Unit,
    onSortSelected: (String) -> Unit,
    onDateFilterClicked: () -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }
    val sortOptions = listOf("Newest", "Oldest", "Severity")

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Filter By Status", fontWeight = FontWeight.Bold)
            FilterChips(selectedStatus = statusFilter, onStatusSelected = onStatusSelected)
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(expanded = showSortMenu, onExpandedChange = { showSortMenu = !showSortMenu }) {
                        OutlinedTextField(
                            value = "Sort by: $sortBy",
                            onValueChange = {}, 
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            leadingIcon = { Icon(Icons.Default.Sort, null, modifier = Modifier.size(18.dp))},
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSortMenu) }
                        )
                        ExposedDropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { onSortSelected(option); showSortMenu = false })
                            }
                        }
                    }
                }
                OutlinedButton(onClick = onDateFilterClicked) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveFiltersRow(dateRange: Pair<Long, Long>?, onClearDateFilter: () -> Unit) {
    if (dateRange != null) {
        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        val startDate = formatter.format(Date(dateRange.first))
        val endDate = formatter.format(Date(dateRange.second))
        
        InputChip(
            selected = true,
            onClick = { /* Maybe open the date picker again? */ },
            label = { Text("Date: $startDate - $endDate") },
            trailingIcon = {
                IconButton(onClick = onClearDateFilter, modifier = Modifier.size(18.dp)) {
                    Icon(Icons.Default.Close, "Clear Date Filter")
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(selectedStatus: String, onStatusSelected: (String) -> Unit) {
    val statuses = listOf("All", "Pending", "Resolved")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        items(statuses) {
            FilterChip(
                selected = it == selectedStatus,
                onClick = { onStatusSelected(it) },
                label = { Text(it) },
                leadingIcon = if(it == selectedStatus) { { Icon(Icons.Default.Check, null) } } else { null }
            )
        }
    }
}

@Composable
fun WelcomeAdminHeader(name: String?) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Welcome back,", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            Text(text = name ?: "Admin", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier, color: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color), elevation = CardDefaults.cardElevation(8.dp)) {
        Box(modifier = Modifier.background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.8f))))) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(28.dp), tint = Color.White)
                Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
fun QuickActionCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(40.dp), tint = Color(0xFF1D3557))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1D3557))
                Text(subtitle, fontSize = 14.sp, color = Color.Gray)
            }
            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun RecentIncidentCard(incident: Incident, onCardClick: () -> Unit, onStatusChange: (String) -> Unit) {
    var showStatusMenu by remember { mutableStateOf(false) }
    val incidentIcon = when(incident.type.lowercase()) {
        "illegal dumping" -> Icons.Default.DeleteSweep
        "overflowing bin" -> Icons.Default.RestoreFromTrash
        "damaged bin" -> Icons.Default.Build
        "littering" -> Icons.Default.Landscape
        else -> Icons.Default.Report
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp).clickable(onClick = onCardClick), verticalAlignment = Alignment.Top) {
                Icon(incidentIcon, contentDescription = incident.type, modifier = Modifier.size(40.dp).padding(end = 12.dp), tint = MaterialTheme.colorScheme.primary)
                Column(modifier = Modifier.weight(1f)) {
                    Text(incident.type, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = "Reported on: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(incident.timestamp))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(getStatusColor(incident.status).copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = incident.status,
                        color = getStatusColor(incident.status),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End) {
                 Box {
                    OutlinedButton(onClick = { showStatusMenu = true }) {
                        Text("Change Status")
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    DropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Mark as In Progress") },
                            onClick = { onStatusChange("In Progress"); showStatusMenu = false },
                            enabled = incident.status != "In Progress"
                        )
                        DropdownMenuItem(
                            text = { Text("Mark as Resolved") },
                            onClick = { onStatusChange("Resolved"); showStatusMenu = false },
                            enabled = incident.status != "Resolved"
                        )
                    }
                }
            }
        }
    }
}
