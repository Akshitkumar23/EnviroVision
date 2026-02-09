package com.example.wastemanagement.screens.report

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wastemanagement.components.ImagePicker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..3).forEach { step ->
            val isCompleted = step < currentStep
            val isCurrent = step == currentStep
            
            val color = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                isCurrent -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            val contentColor = when {
                isCompleted -> MaterialTheme.colorScheme.onPrimary
                isCurrent -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                if(isCompleted) {
                    Icon(Icons.Default.Done, contentDescription = "Completed", tint = contentColor)
                } else {
                    Text(text = step.toString(), color = contentColor, fontWeight = FontWeight.Bold)
                }
            }

            if (step < 3) {
                Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentScreen(
    navController: NavController,
    viewModel: ReportIncidentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // The entire form is now a single step process, managed within Step2Details
    var currentStep by remember { mutableStateOf(1) } // Simplified, but we can manage with just one screen

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if(viewModel.isEditing) "Edit Incident" else "Report an Incident") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
        // Directly show the main content screen
        MainReportScreen(paddingValues, viewModel, locationPermissionsState.allPermissionsGranted) {
             viewModel.submitOrUpdateReport(context) {
                navController.previousBackStackEntry?.savedStateHandle?.set("needs_refresh", true)
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun MainReportScreen(
    paddingValues: PaddingValues,
    viewModel: ReportIncidentViewModel,
    permissionsGranted: Boolean,
    onSubmit: () -> Unit
) {
    val incidentTypes = listOf(
        "Solid Waste",
        "Water Pollution",
        "Noise Pollution",
        "Illegal Dumping",
        "Other"
    )
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Describe the Incident", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Describe the scene, what you see, etc.") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            minLines = 4
        )

        // AI Generation Button and Category Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.generateWithAi() },
                enabled = !viewModel.isAiLoading && viewModel.description.isNotBlank()
            ) {
                if(viewModel.isAiLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Generate with AI")
                }
            }

            // Category Selector Dropdown
            Box {
                OutlinedButton(
                    onClick = { categoryDropdownExpanded = true },
                    enabled = viewModel.incidentType.isNotBlank()
                ) {
                    Text(viewModel.incidentType.ifBlank { "Select Category" })
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category")
                }
                DropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    incidentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = { 
                                viewModel.onIncidentTypeChange(type)
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (permissionsGranted) {
             Column {
                Text("Location (Drag marker to adjust)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(viewModel.location, 15f) }
                val markerState = rememberMarkerState(position = viewModel.location)
                
                LaunchedEffect(markerState.position) {
                    viewModel.onLocationUpdate(markerState.position)
                }
                
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(state = markerState, draggable = true)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = "Address", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(viewModel.address, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            Text("Location permission not granted.", textAlign = TextAlign.Center)
        }
        
        Text("Add Proof & Severity", style = MaterialTheme.typography.titleMedium)
        ImagePicker(
            existingImageUris = viewModel.imageUris,
            onImagesSelected = { viewModel.onImagesSelected(it) },
            onImageRemoved = { viewModel.onImageRemoved(it) }
        )
        SeveritySelector(selectedSeverity = viewModel.severity, onSeveritySelected = { viewModel.onSeverityChange(it) })
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onSubmit, 
            enabled = !viewModel.isLoading, 
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
             if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(if (viewModel.isEditing) "Update Report" else "Submit Report", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun SeveritySelector(selectedSeverity: String, onSeveritySelected: (String) -> Unit) {
    val severities = listOf("Low", "Medium", "High")
    Column {
        Text("How severe is it?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            severities.forEach { severity ->
                val isSelected = selectedSeverity == severity
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSeveritySelected(severity) }
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        severity, 
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if(isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
