package com.example.wastemanagement.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.ui.theme.PrimaryBlue
import com.example.wastemanagement.ui.theme.AccentTeal
import com.example.wastemanagement.ui.theme.SecondaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController, viewModel: StatisticsViewModel = hiltViewModel()) {
    val allIncidents by viewModel.allIncidents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incident Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (allIncidents.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "No incident data available. Report an incident to see statistics.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                val incidentsByType = allIncidents.groupBy { it.type }
                val incidentsByStatus = allIncidents.groupBy { it.status }

                item { StatsSummary(allIncidents) }
                item { PieChartCard(incidentsByType) }
                item { BarChartCard(incidentsByStatus) }
            }
        }
    }
}

@Composable
fun StatsSummary(incidents: List<Incident>) {
    val total = incidents.size
    val pending = incidents.count { it.status.equals("Reported", ignoreCase = true) || it.status.equals("In Progress", ignoreCase = true) }
    val resolved = incidents.count { it.status.equals("Resolved", ignoreCase = true) }
    
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        StatCard(label = "Total", value = total.toString(), color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.padding(8.dp))
        StatCard(label = "Pending", value = pending.toString(), color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.padding(8.dp))
        StatCard(label = "Resolved", value = resolved.toString(), color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(text = label, fontSize = 16.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun PieChartCard(incidentsByType: Map<String, List<Incident>>) {
    val chartColors = listOf(PrimaryBlue, AccentTeal, SecondaryBlue, Color.Gray, Color.Magenta)
    val chartData = remember(incidentsByType) {
        incidentsByType.map { (type, incidents) -> type to incidents.size.toFloat() }
    }
    val total = chartData.sumOf { it.second.toDouble() }.toFloat()

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Incidents by Type", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            if (chartData.isEmpty()) {
                Text("No data for this chart.")
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        var startAngle = 0f
                        chartData.forEachIndexed { index, (type, value) ->
                            val sweepAngle = (value / total) * 360f
                            drawArc(
                                color = chartColors[index % chartColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        chartData.forEachIndexed { index, (type, value) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(chartColors[index % chartColors.size], CircleShape))
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text("$type (${value.toInt()})")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarChartCard(incidentsByStatus: Map<String, List<Incident>>) {
    val chartData = remember(incidentsByStatus) {
        incidentsByStatus.map { (status, incidents) -> status to incidents.size.toFloat() }
    }
    val maxValue = chartData.maxOfOrNull { it.second } ?: 0f

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Incidents by Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            if (chartData.isEmpty()) {
                Text("No data for this chart.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    chartData.forEach { (status, value) ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(status, modifier = Modifier.weight(0.3f))
                            Row(modifier = Modifier.weight(0.7f).height(24.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))) {
                                 Box(modifier = Modifier
                                     .fillMaxWidth(fraction = if (maxValue > 0) value / maxValue else 0f)
                                     .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)))
                            }
                            Text(value.toInt().toString(), modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
