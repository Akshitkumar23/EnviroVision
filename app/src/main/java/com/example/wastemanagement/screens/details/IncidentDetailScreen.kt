package com.example.wastemanagement.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wastemanagement.components.ConfirmationDialog
import com.example.wastemanagement.components.ZoomableImage
import com.example.wastemanagement.components.getSeverityColor
import com.example.wastemanagement.components.getStatusColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(
    navController: NavController,
    viewModel: IncidentDetailsViewModel = hiltViewModel()
) {
    val incident by viewModel.incident.collectAsState()
    val reporter by viewModel.reporter.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incident Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("report_incident?incidentId=${incident?.id}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Saffron color
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            incident?.let { incident ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp) // Increased spacing
                ) {
                    Text(incident.type, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                    // Status Badge
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(getStatusColor(incident.status)))
                        Text(incident.status, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    }

                    // Info Card
                    Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoRow(icon = Icons.Default.Person, label = "Reported by", value = reporter?.displayName ?: "Loading...")
                            InfoRow(icon = Icons.Default.CalendarToday, label = "Date & Time", value = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(incident.timestamp)))
                            InfoRow(icon = Icons.Default.Warning, label = "Severity", value = incident.severity, valueColor = getSeverityColor(incident.severity))
                        }
                    }

                    Text(text = incident.description, style = MaterialTheme.typography.bodyLarge)

                    if (incident.imageUris.isNotEmpty()) {
                        Text("Photos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(incident.imageUris) { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Incident image",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedImageUri = uri },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    val location = remember(incident.location) {
                        val parts = incident.location.split(",")
                        if (parts.size == 2) {
                            val lat = parts[0].trim().toDoubleOrNull()
                            val lon = parts[1].trim().toDoubleOrNull()
                            if (lat != null && lon != null) LatLng(lat, lon) else null
                        } else null
                    }

                    if(location != null) {
                        Text("Location", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(location, 15f) }
                        val markerState = rememberMarkerState(position = location)

                        Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(12.dp))) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = com.google.maps.android.compose.MapUiSettings(zoomControlsEnabled = false)
                            ) {
                                Marker(state = markerState)
                            }
                        }
                    }

                    incident.afterImageUri?.let {
                        Text("After Photo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        AsyncImage(
                            model = it,
                            contentDescription = "After photo provided by admin",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedImageUri = it },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        if (showDeleteConfirmation) {
            ConfirmationDialog(
                title = "Delete Incident",
                message = "Are you sure you want to delete this incident report?",
                onConfirm = {
                    viewModel.deleteIncident()
                    showDeleteConfirmation = false
                    navController.popBackStack()
                },
                onDismiss = { showDeleteConfirmation = false }
            )
        }

        selectedImageUri?.let {
            Dialog(onDismissRequest = { selectedImageUri = null }) {
                ZoomableImage(model = it)
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, valueColor: androidx.compose.ui.graphics.Color? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold, color = valueColor ?: MaterialTheme.colorScheme.onSurface)
    }
}
