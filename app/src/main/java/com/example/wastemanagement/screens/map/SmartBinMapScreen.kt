package com.example.wastemanagement.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartBinMapScreen(
    navController: NavController,
    viewModel: SmartBinMapViewModel = hiltViewModel()
) {
    val bins by viewModel.bins.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewModel.delhiLatLng, 11f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Bins Near Me") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                bins.forEach { bin ->
                    Marker(
                        state = MarkerState(position = LatLng(bin.latitude, bin.longitude)),
                        title = bin.name,
                        snippet = "Fill Level: ${bin.fillLevel}%",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            getBinColor(bin.fillLevel)
                        )
                    )
                }
            }
            // TODO: Add filters for fill level and distance radius here later
        }
    }
}

fun getBinColor(fillLevel: Int): Float {
    return when {
        fillLevel < 50 -> BitmapDescriptorFactory.HUE_GREEN
        fillLevel < 85 -> BitmapDescriptorFactory.HUE_YELLOW
        else -> BitmapDescriptorFactory.HUE_RED
    }
}
