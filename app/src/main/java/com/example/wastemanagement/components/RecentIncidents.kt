package com.example.wastemanagement.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.wastemanagement.data.Incident

@Composable
fun RecentIncidents(
    incidents: List<Incident>,
    navController: NavController,
    onDelete: (Incident) -> Unit
) {
    Column {
        Text(
            text = "Recent Incidents",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (incidents.isEmpty()) {
            Text("No recent incidents reported.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(incidents) { incident ->
                    IncidentCard(incident = incident, navController = navController, onDelete = onDelete)
                }
            }
        }
    }
}

@Composable
fun IncidentCard(
    incident: Incident,
    navController: NavController,
    onDelete: (Incident) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("report_incident?incidentId=${incident.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = incident.imageUris.firstOrNull(),
                contentDescription = "Incident image",
                modifier = Modifier.size(60.dp).padding(end = 8.dp),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
                },
                error = {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
                }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = incident.type, fontWeight = FontWeight.Bold)
                Text(text = incident.description, maxLines = 1)
                Text(text = "Status: ${incident.status}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
