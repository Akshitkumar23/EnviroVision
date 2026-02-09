package com.example.wastemanagement.screens.feature

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wastemanagement.screens.feature.components.SeverityTag

@Composable
fun AdminIncidentListItem(incident: Incident, onAction: (Incident, String) -> Unit) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(incident.description, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Text("Reported by: ${incident.reportedBy} on ${incident.date}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SeverityTag(severity = incident.severity)
                
                Box {
                    Button(onClick = { showStatusMenu = true }) {
                        Text(incident.status)
                        Icon(Icons.Default.ArrowDropDown, "Change Status")
                    }
                    DropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                        DropdownMenuItem(text = { Text("Open") }, onClick = {
                            onAction(incident, "Open")
                            showStatusMenu = false
                        })
                        DropdownMenuItem(text = { Text("In Progress") }, onClick = {
                            onAction(incident, "In Progress")
                            showStatusMenu = false
                        })
                        DropdownMenuItem(text = { Text("Resolved") }, onClick = {
                            onAction(incident, "Resolved")
                            showStatusMenu = false
                        })
                    }
                }
            }
        }
    }
}
