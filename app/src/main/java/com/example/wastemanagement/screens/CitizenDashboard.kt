package com.example.wastemanagement.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Brightness6
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wastemanagement.components.IncidentCategoryTabs
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.screens.login.AuthViewModel
import com.example.wastemanagement.screens.statistics.StatisticsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenDashboard(
    navController: NavController,
    onMenuClick: () -> Unit,
    statsViewModel: StatisticsViewModel = hiltViewModel()
) {
    val user = Firebase.auth.currentUser
    val weeklyIncidents by statsViewModel.weeklyIncidents.collectAsState()
    val filteredIncidents by statsViewModel.filteredIncidents.collectAsState()
    val selectedIncidentType by statsViewModel.selectedIncidentType.collectAsState()
    val incidentsByStatus by statsViewModel.incidentsByStatus.collectAsState()
    val allIncidents by statsViewModel.allIncidents.collectAsState()

    val incidentTypes = listOf("All", "Solid Waste", "Water Pollution", "Noise Pollution", "Illegal Dumping")

    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Citizen Dashboard") }, 
            navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Menu") } },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("report_incident") },
                containerColor = MaterialTheme.colorScheme.tertiary, // Use theme accent color
                contentColor = MaterialTheme.colorScheme.onTertiary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Filled.Add, "Report Incident", modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { WelcomeHeader(userName = user?.displayName ?: "Citizen", modifier = Modifier.padding(horizontal = 16.dp)) }
            item { MetricCardsRow(incidentsByStatus, allIncidents.size) }
            item { SectionHeader("Quick Actions", Modifier.padding(horizontal = 16.dp)) }
            item { QuickActionsRow(navController, Modifier.padding(horizontal = 16.dp)) }
            item { MapPreviewCard(modifier = Modifier.padding(horizontal = 16.dp)) }
            item { TrendChartCard(weeklyIncidents, modifier = Modifier.padding(horizontal = 16.dp)) }
            item { SectionHeader("Recent Incidents", Modifier.padding(horizontal = 16.dp)) }
            item { 
                IncidentCategoryTabs(
                    categories = incidentTypes, 
                    selectedCategory = selectedIncidentType, 
                    onCategorySelected = { statsViewModel.onIncidentTypeSelected(it) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) 
            }
            items(filteredIncidents) { incident -> // Using filtered data
                RecentIncidentItem(incident, Modifier.padding(horizontal = 16.dp))
            }
            item { TipsBanner(modifier = Modifier.padding(horizontal = 16.dp)) }
            item { Spacer(modifier = Modifier.height(80.dp)) } // Bottom padding for FAB
        }
    }
}

@Composable
fun WelcomeHeader(userName: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
                Text(
                    text = "Welcome, $userName!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sdf.format(Date()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Outlined.WavingHand,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MetricCardsRow(incidentsByStatus: Map<String, Int>, totalIncidents: Int) {
    val openIssues = incidentsByStatus["Reported"] ?: 0
    val resolvedIssues = incidentsByStatus["Resolved"] ?: 0
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item { MetricCard("Total Incidents", totalIncidents.toString(), Icons.Outlined.Report, MaterialTheme.colorScheme.primary) }
        item { MetricCard("Open Issues", openIssues.toString(), Icons.Outlined.WatchLater, MaterialTheme.colorScheme.error) }
        item { MetricCard("Resolved", resolvedIssues.toString(), Icons.Outlined.CheckCircle, MaterialTheme.colorScheme.tertiary) }
        item { 
            Box(contentAlignment = Alignment.Center) {
                MetricCard("Nearby Bins", "0", Icons.Outlined.Map, MaterialTheme.colorScheme.secondary)
                val infiniteTransition = rememberInfiniteTransition()
                val arrowOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Icon(
                    imageVector = Icons.Filled.ArrowForward, // Replaced AutoMirrored icon
                    contentDescription = "Scroll for more",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.CenterEnd).offset(x = arrowOffset.dp)
                )
            }
        }
    }
}

@Composable
fun MapPreviewCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().height(150.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)) {
            Icon(Icons.Outlined.Map, "Map Preview", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Map Preview of Nearby Bins", modifier = Modifier.padding(8.dp).align(Alignment.BottomCenter), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun TrendChartCard(weeklyIncidents: Map<String, Int>, modifier: Modifier = Modifier) {
    val yyyyMMddFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val dayOfWeekFormat = SimpleDateFormat("E", Locale.getDefault())

    val today = Calendar.getInstance()
    val maxIncidents = weeklyIncidents.values.maxOrNull()?.coerceAtLeast(1) ?: 1

    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Weekly Incident Trends", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        text = monthYearFormat.format(today.time),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            if (weeklyIncidents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("No incident data for the last 7 days.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly, 
                    verticalAlignment = Alignment.Bottom
                ) {
                    for (i in 6 downTo 0) {
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_YEAR, -i)
                        val date = calendar.time

                        val dateString = yyyyMMddFormat.format(date)
                        val incidentCount = weeklyIncidents[dateString] ?: 0
                        val barHeight = incidentCount.toFloat() / maxIncidents

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = incidentCount.toString(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                Modifier
                                    .width(30.dp) // Wider bars
                                    .fillMaxHeight(barHeight)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                            )
                            Text(
                                text = dayOfWeekFormat.format(date),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentIncidentItem(incident: Incident, modifier: Modifier = Modifier) {
    val statusColor = when (incident.status) {
        "In Progress" -> MaterialTheme.colorScheme.secondary
        "Resolved" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Delete, null, Modifier.size(40.dp).padding(4.dp), tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(incident.type, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(incident.description, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(incident.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = incident.status,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.background(statusColor, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActionsRow(navController: NavController, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        QuickActionIcon(icon = Icons.Outlined.ListAlt, label = "My Reports") { navController.navigate("my_reports") }
        QuickActionIcon(icon = Icons.Outlined.Map, label = "Bins Near Me") { navController.navigate("smart_bin_map") }
        QuickActionIcon(icon = Icons.Outlined.Forest, label = "Drives") { navController.navigate("plantation_drives") }
        QuickActionIcon(icon = Icons.Outlined.BarChart, label = "Statistics") { navController.navigate("statistics") }
        QuickActionIcon(icon = Icons.Outlined.Settings, label = "Settings") { navController.navigate("settings") }
    }
}

@Composable
fun QuickActionIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
    }
}

@Composable
fun TipsBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Lightbulb, null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(Modifier.width(12.dp))
            Text("Did you know? Segregating waste at the source helps improve recycling rates significantly!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.size(width = 140.dp, height = 120.dp), 
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.8f))))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(32.dp))
                Column {
                    Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = modifier.padding(top = 16.dp, bottom = 8.dp))
}
