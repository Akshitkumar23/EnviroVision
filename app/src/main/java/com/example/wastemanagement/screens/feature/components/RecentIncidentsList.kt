package com.example.wastemanagement.screens.feature.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Sample data class for an incident
data class SampleIncident(val icon: ImageVector, val summary: String, val date: String, val status: String, val severity: String)

@Composable
fun RecentIncidentsList(navController: NavController) {
    val recentIncidents = listOf(
        SampleIncident(Icons.Default.Delete, "Overflowing bin on Main St", "2h ago", "Open", "High"),
        SampleIncident(Icons.Default.WaterDrop, "Illegal dumping near park", "1d ago", "In Progress", "Medium"),
        SampleIncident(Icons.Default.BrokenImage, "Broken pavement, sector 12", "3d ago", "Resolved", "Low")
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Recent Incidents", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        recentIncidents.forEach {
            IncidentListItem(incident = it) {
                // TODO: Navigate to Incident Details screen with incident ID
            }
        }
    }
}

@Composable
fun IncidentListItem(incident: SampleIncident, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(incident.icon, contentDescription = incident.summary, modifier = Modifier.size(36.dp))
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(incident.summary, fontWeight = FontWeight.SemiBold)
                Text(incident.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(incident.status, style = MaterialTheme.typography.bodySmall)
                SeverityTag(severity = incident.severity)
            }
        }
    }
}
