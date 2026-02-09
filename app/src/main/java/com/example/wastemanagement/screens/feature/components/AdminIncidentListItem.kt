package com.example.wastemanagement.screens.feature.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wastemanagement.screens.feature.data.Incident

@Composable
fun AdminIncidentListItem(incident: Incident, onAction: (Incident, String) -> Unit) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(incident.description, fontWeight = FontWeight.SemiBold)
            Text("Reported by: ${incident.reportedBy} on ${incident.date}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                SeverityTag(severity = incident.severity)
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
