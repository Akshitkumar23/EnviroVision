package com.example.wastemanagement.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.components.InfoChip
import com.example.wastemanagement.components.getSeverityColor
import com.example.wastemanagement.components.getStatusColor
import com.example.wastemanagement.components.shimmerBackground
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailsScreen(
    navController: NavController,
    viewModel: IncidentDetailsViewModel = hiltViewModel()
) {
    val incident by viewModel.incident.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { shouldNavigate ->
            if (shouldNavigate) {
                navController.popBackStack()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (incident == null) {
            IncidentDetailsLoadingSkeleton()
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
                    IncidentHeader(incidentData, modifier = Modifier.padding(16.dp))
                }

                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IncidentInfoCard(incidentData)
                    IncidentDescriptionCard(incidentData.description)

                    if (!incidentData.masterIncidentId.isNullOrBlank()) {
                        MergedInfoCard(incidentData.masterIncidentId!!)
                    }

                    PhotoSection(incidentData.imageUris, "Your Submitted Photos") { uri -> selectedImageUri = uri }

                    if (!incidentData.afterImageUri.isNullOrBlank()) {
                        PhotoSection(listOf(incidentData.afterImageUri!!), "Resolution Photo") { uri -> selectedImageUri = uri }
                    }

                    MapSection(incidentData.location)
                }
            }

            val toolbarHeight = 64.dp
            val toolbarHeightPx = with(density) { toolbarHeight.toPx() } 
            val transitionStart = imageHeightPx - (toolbarHeightPx * 2)
            val transitionEnd = imageHeightPx - toolbarHeightPx
            val transitionProgress = ((scrollState.value - transitionStart) / (transitionEnd - transitionStart)).coerceIn(0f, 1f)

            val topBarColor = lerp(
                Color.Transparent,
                MaterialTheme.colorScheme.surface,
                transitionProgress
            )
            val topBarContentColor = lerp(
                Color.White,
                MaterialTheme.colorScheme.onSurface,
                transitionProgress
            )

            TopAppBar(
                title = { Text(incidentData.type, color = topBarContentColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = topBarContentColor)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Incident", tint = topBarContentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
            )
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(onConfirm = {
            viewModel.deleteIncident()
            showDeleteDialog = false
        }, onDismiss = { showDeleteDialog = false })
    }

    if (selectedImageUri != null) {
        ZoomableImageDialog(uri = selectedImageUri!!, onDismiss = { selectedImageUri = null })
    }
}

@Composable
fun IncidentHeader(incident: Incident, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(incident.type, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)){
            Icon(Icons.Default.CalendarToday, contentDescription = "Date", modifier = Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.8f))
            Text(
                text = "Reported on: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(incident.timestamp))}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun IncidentInfoCard(incident: Incident) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InfoChip("Status", incident.status, getStatusColor(incident.status))
            InfoChip("Severity", incident.severity, getSeverityColor(incident.severity))
        }
    }
}

@Composable
fun IncidentDescriptionCard(description: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun MergedInfoCard(masterId: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Info, contentDescription = "Merged Info")
            Text("This report has been merged into incident #$masterId")
        }
    }
}

@Composable
fun PhotoSection(imageUris: List<String>, title: String, onImageClick: (String) -> Unit) {
    if (imageUris.isNotEmpty() && imageUris.first().isNotBlank()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(imageUris) { uriString ->
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerBackground(RoundedCornerShape(12.dp))
                            .clickable { onImageClick(uriString) }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uriString)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Incident Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapSection(locationString: String) {
    val location = remember(locationString) {
        if (locationString.isNotBlank()) {
            val parts = locationString.split(",").map { it.trim() }
            if (parts.size == 2) {
                val lat = parts[0].toDoubleOrNull()
                val lon = parts[1].toDoubleOrNull()
                if(lat != null && lon != null) LatLng(lat, lon) else null
            } else null
        } else null
    }

    if (location != null) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth().height(250.dp), shape = RoundedCornerShape(12.dp)) {
                val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(location, 15f) }
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(state = rememberMarkerState(position = location))
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, contentDescription = "Warning") },
        title = { Text("Delete Incident?") },
        text = { Text("Are you sure you want to permanently delete this incident report? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Delete") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ZoomableImageDialog(uri: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f))) {
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            AsyncImage(
                model = uri,
                contentDescription = "Zoomed Incident Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 5f)
                            offset += pan
                        }
                    }
                    .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offset.x, translationY = offset.y),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
    }
}

@Composable
fun IncidentDetailsLoadingSkeleton() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Spacer(modifier = Modifier.fillMaxWidth().height(300.dp).shimmerBackground(RoundedCornerShape(0.dp)))
        Spacer(modifier = Modifier.fillMaxWidth().height(32.dp).shimmerBackground(RoundedCornerShape(8.dp)))
        Spacer(modifier = Modifier.fillMaxWidth(0.5f).height(20.dp).shimmerBackground(RoundedCornerShape(8.dp)))
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
                Spacer(modifier = Modifier.size(80.dp, 30.dp).shimmerBackground(RoundedCornerShape(8.dp)))
                Spacer(modifier = Modifier.size(80.dp, 30.dp).shimmerBackground(RoundedCornerShape(8.dp)))
            }
        }
    }
}
