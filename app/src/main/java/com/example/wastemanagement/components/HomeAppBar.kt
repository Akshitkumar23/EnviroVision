package com.example.wastemanagement.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.wastemanagement.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    onNotificationClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
            }
        }
    )
}
