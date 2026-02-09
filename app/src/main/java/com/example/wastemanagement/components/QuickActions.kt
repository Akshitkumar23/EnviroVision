package com.example.wastemanagement.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickActions() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { /* TODO */ }) {
            // Replaced AutoMirrored icon with a default one
            Icon(Icons.Filled.List, contentDescription = "My Reports")
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Filled.LocationOn, contentDescription = "Nearby Bins")
        }
        IconButton(onClick = { /* TODO */ }) {
            // Replaced AutoMirrored icon with a default one
            Icon(Icons.Filled.Help, contentDescription = "Help/FAQ")
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    }
}
