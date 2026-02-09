package com.example.wastemanagement.screens.admindetail

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wastemanagement.components.ImagePicker
import com.example.wastemanagement.components.ZoomableImageDialog
import com.example.wastemanagement.components.getSeverityColor
import com.example.wastemanagement.components.getStatusColor
import com.example.wastemanagement.data.Incident
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
fun AdminIncidentDetailScreen(
    navController: NavController,
    viewModel: AdminIncidentDetailViewModel = hiltViewModel()
) {
    val incident by viewModel.incident.collectAsState()
    val reporter by viewModel.reporter.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    var showStatusSelector by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    val afterPhotoUris by viewModel.afterPhotoUris.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (incident == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            val incidentData = incident!!
            val scrollState = rememberScrollState()
            val imageHeight = 300.dp
            val density = LocalDensity.current
            val imageHeightPx = with(density) { imageHeight.toPx() }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .graphicsLayer {
                            alpha = 1f - (scrollState.value / imageHeightPx).coerceIn(0f, 1f)
                            translationY = scrollState.value * 0.5f
                        },
                    contentAlignment = Alignment.BottomStart
                ) {
                    AsyncImage(
                        model = incidentData.imageUris.firstOrNull(),
                        contentDescription = "Incident Header",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
                    Text(incidentData.type, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(16.dp))
                }

                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    StatusChip(status = selectedStatus, onClick = { showStatusSelector = true })

                    Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoRow(icon = Icons.Default.Person, label = "Reported by", value = reporter?.displayName ?: "Loading...")
                            InfoRow(icon = Icons.Default.CalendarToday, label = "Date & Time", value = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(incidentData.timestamp)))
                            InfoRow(icon = Icons.Default.Warning, label = "Severity", value = incidentData.severity, valueColor = getSeverityColor(incidentData.severity))
                        }
                    }

                    Text(incidentData.description, style = MaterialTheme.typography.bodyLarge)
                    
                    if (incidentData.imageUris.isNotEmpty()) {
                        Text("User Submitted Photos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(incidentData.imageUris) { uri ->
                                AsyncImage(
                                    model = uri, contentDescription = "Incident image",
                                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).clickable { selectedImageUri = uri },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    
                    val location = remember(incidentData.location) {
                        val parts = incidentData.location.split(",")
                        if (parts.size == 2) {
                            val lat = parts[0].trim().toDoubleOrNull()
                            val lon = parts[1].trim().toDoubleOrNull()
                            if (lat != null && lon != null) LatLng(lat, lon) else null
                        } else null
                    }

                    if(location != null) {
                        Text("Location", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(location, 15f) }
                        GoogleMap(
                            modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)),
                            cameraPositionState = cameraPositionState
                        ) {
                            Marker(state = rememberMarkerState(position = location))
                        }
                    }

                    Column {
                        Text("Resolution Photos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        ImagePicker(existingImageUris = afterPhotoUris, onImagesSelected = { viewModel.onAfterPhotosSelected(it) }, onImageRemoved = { viewModel.onAfterPhotoRemoved(it) })
                    }
                }
            }
            
            val toolbarHeightPx = with(density) { 64.dp.toPx() }
            val transitionStart = imageHeightPx - (toolbarHeightPx * 2)
            val transitionEnd = imageHeightPx - toolbarHeightPx
            val transitionProgress = ((scrollState.value - transitionStart) / (transitionEnd - transitionStart)).coerceIn(0f, 1f)

            val topBarColor = lerp(Color.Transparent, MaterialTheme.colorScheme.surface, transitionProgress)
            val topBarContentColor = lerp(Color.White, MaterialTheme.colorScheme.onSurface, transitionProgress)

            TopAppBar(
                title = { Text(incidentData.type, color = topBarContentColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = topBarContentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
            )
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomEnd) {
             FloatingActionButton(
                onClick = { viewModel.updateIncidentStatus(context) },
                containerColor = Color(0xFFF57F17), 
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Done, "Update Status")
            }
        }

        if (showStatusSelector) {
            StatusSelectorDialog(
                onStatusSelected = { status ->
                    viewModel.onStatusSelected(status)
                    showStatusSelector = false
                },
                onDismiss = { showStatusSelector = false }
            )
        }

        selectedImageUri?.let {
            ZoomableImageDialog(uri = it) { selectedImageUri = null }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, valueColor: Color? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold, color = valueColor ?: MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun StatusChip(status: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(getStatusColor(status))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(status, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSelectorDialog(onStatusSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val statuses = listOf("Reported", "In Progress", "Resolved", "Rejected")
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Update Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                statuses.forEach { status ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        onClick = { onStatusSelected(status) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(getStatusColor(status)))
                            Text(text = status, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
