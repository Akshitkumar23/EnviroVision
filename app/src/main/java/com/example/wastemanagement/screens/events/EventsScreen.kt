package com.example.wastemanagement.screens.events

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wastemanagement.data.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: EventViewModel = hiltViewModel()
) {
    val upcomingEvents by viewModel.upcomingEvents.collectAsState()
    val completedEvents by viewModel.completedEvents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Events & Drives") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Use theme color
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Upcoming Events", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            if (upcomingEvents.isEmpty()) {
                item {
                    Text("No upcoming events at the moment. Stay tuned!", modifier = Modifier.padding(vertical = 16.dp))
                }
            } else {
                items(upcomingEvents) { event ->
                    EventCard(event = event, onRsvpClick = { viewModel.rsvpToEvent(event.id, "current_user_id") }) // Placeholder user ID
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Past Events", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            if (completedEvents.isEmpty()) {
                item {
                    Text("No past events to show.", modifier = Modifier.padding(vertical = 16.dp))
                }
            } else {
                items(completedEvents) { event ->
                    EventCard(event = event, isCompleted = true, onUploadProofClick = {}) // Placeholder action
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    isCompleted: Boolean = false,
    onRsvpClick: () -> Unit = {},
    onUploadProofClick: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if(isCompleted) Color.LightGray.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(event.description, style = MaterialTheme.typography.bodyMedium)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Date", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(dateFormat.format(Date(event.date)), style = MaterialTheme.typography.bodySmall)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(event.location, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            if (!isCompleted) {
                Button(
                    onClick = onRsvpClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RSVP Now (${event.participants.size}/${event.registrationLimit})")
                }
            } else {
                OutlinedButton(
                    onClick = onUploadProofClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Proof of Participation")
                }
            }
        }
    }
}
