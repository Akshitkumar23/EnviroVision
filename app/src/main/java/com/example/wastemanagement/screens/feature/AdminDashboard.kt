package com.example.wastemanagement.screens.feature

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wastemanagement.components.AdminMessageBanner
import com.example.wastemanagement.components.getStatusColor
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.screens.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val incidents by viewModel.incidents.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues).fillMaxSize()) {
            AdminMessageBanner(userRole = userRole)

            if(incidents.isEmpty()){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text("No incidents reported yet.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(incidents, key = { incident -> incident.id }) { incident ->
                        AdminIncidentListItem(incident = incident, viewModel = viewModel, navController = navController)
                    }
                }
            }
        }
    }

    viewModel.showStatusDialog.value?.let { 
        StatusUpdateDialog(incident = it, viewModel = viewModel)
    }

    viewModel.showAfterPhotoDialog.value?.let { 
        AfterPhotoUploadDialog(incident = it, viewModel = viewModel)
    }
}

@Composable
fun AdminIncidentListItem(
    incident: Incident,
    viewModel: AdminViewModel,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { 
            // FIX: Navigate to the read-only admin detail screen
            navController.navigate("admin_incident_detail/${incident.id}") 
        },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(incident.type, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reported on: ${incident.date}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        modifier = Modifier.clickable { expanded = true }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Update Status") },
                            onClick = {
                                viewModel.showStatusDialog.value = incident
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Upload After Photo") },
                            onClick = {
                                viewModel.showAfterPhotoDialog.value = incident
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(getStatusColor(incident.status)))
                Text(incident.status, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = incident.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusUpdateDialog(incident: Incident, viewModel: AdminViewModel) {
    val statuses = listOf("Reported", "In Progress", "Resolved", "Rejected")
    var newStatus by remember { mutableStateOf(incident.status) }

    AlertDialog(
        onDismissRequest = { viewModel.showStatusDialog.value = null },
        title = { Text("Update Status") },
        text = {
            Column {
                Text("Select the new status for the incident:")
                Spacer(modifier = Modifier.height(16.dp))
                statuses.forEach { status ->
                    Row(
                        Modifier.fillMaxWidth().clickable { newStatus = status }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (status == newStatus),
                            onClick = { newStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = status)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.updateIncidentStatus(incident, newStatus)
                viewModel.showStatusDialog.value = null
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = { viewModel.showStatusDialog.value = null }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AfterPhotoUploadDialog(incident: Incident, viewModel: AdminViewModel) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = { viewModel.showAfterPhotoDialog.value = null },
        title = { Text("Upload After Photo") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected after photo",
                        modifier = Modifier.size(150.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Change Image")
                    }
                } else {
                    Text(
                        "Select an image to upload as an 'after' photo. This will be shown to the user.",
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Select Image")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    imageUri?.let {
                        viewModel.uploadAfterPhoto(incident, it)
                        viewModel.showAfterPhotoDialog.value = null
                    }
                },
                enabled = imageUri != null
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            Button(onClick = { viewModel.showAfterPhotoDialog.value = null }) {
                Text("Cancel")
            }
        }
    )
}
