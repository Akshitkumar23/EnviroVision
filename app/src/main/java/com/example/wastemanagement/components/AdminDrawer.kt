package com.example.wastemanagement.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wastemanagement.screens.login.AuthViewModel

@Composable
fun AdminDrawer(
    onClose: () -> Unit, 
    onItemClick: (String) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val user by authViewModel.user.collectAsState()

    ModalDrawerSheet(drawerContainerColor = Color(0xFF1C2B36)) { // Dark cool color for admin
        Column {
            // Header with Close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Profile",
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        tint = Color.White
                    )
                    Column {
                        Text(user?.email ?: "Admin User", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("System Administrator", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close Drawer", tint = Color.White)
                }
            }

            // Feature List
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(adminFeatures) { category ->
                    AdminFeatureGroup(category = category, onItemClick = onItemClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AdminFeatureItem(feature = AdminNavFeature("User Management", "Manage user roles", Icons.Default.Group, "users_screen"), onItemClick = onItemClick)
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminFeatureItem(feature = AdminNavFeature("Logout", "Sign out from your account", Icons.Default.Logout, "logout"), onItemClick = onItemClick)
                }
            }
        }
    }
}

// Admin-specific data classes
data class AdminNavFeature(val title: String, val description: String, val icon: ImageVector, val route: String? = null)
data class AdminFeatureCategory(val title: String, val features: List<AdminNavFeature>)

// Admin feature list based on user request
val adminFeatures = listOf(
    AdminFeatureCategory(
        title = "Operations Management",
        features = listOf(
            AdminNavFeature("AI Complaint Routing", "Automatic assignment of complaints to relevant authorities", Icons.Default.Share),
            AdminNavFeature("Smart Resource Allocation", "Predictive analytics for staff and equipment distribution", Icons.Default.Construction),
            AdminNavFeature("Real-Time Fleet Management", "GPS tracking and route optimization for collection vehicles", Icons.Default.LocalShipping)
        )
    ),
    AdminFeatureCategory(
        title = "Data Analytics & Intelligence",
        features = listOf(
            AdminNavFeature("Waste Generation Forecasting", "ML models to predict waste volumes and patterns", Icons.Default.Analytics),
            AdminNavFeature("Performance Analytics", "KPI dashboards for collection efficiency, recycling rates, etc.", Icons.Default.Assessment),
            AdminNavFeature("Environmental Impact", "Real-time tracking of pollution levels and waste-to-energy metrics", Icons.Default.GpsFixed)
        )
    ),
    AdminFeatureCategory(
        title = "Advanced Admin Tools",
        features = listOf(
            AdminNavFeature("IoT Sensor Integration", "Monitor smart bin fill levels and equipment status", Icons.Default.Sensors),
            AdminNavFeature("Compliance Management", "Automated reporting for environmental regulations", Icons.Default.Security),
            AdminNavFeature("Event Management", "Create, manage, and track community drives and campaigns", Icons.Default.Event, "events_screen"),
            AdminNavFeature("Inter-Department Collaboration", "Integrated communication system with other civic departments", Icons.Default.GroupWork)
        )
    ),
    AdminFeatureCategory(
        title = "Future-Tech Integration",
        features = listOf(
            AdminNavFeature("Blockchain Waste Tracking", "Immutable record of waste processing from collection to disposal", Icons.Default.Dashboard),
            AdminNavFeature("AI Policy Recommendations", "System-generated suggestions for improving waste management policies", Icons.Default.Policy),
            AdminNavFeature("Predictive Maintenance", "AI alerts for equipment servicing before breakdowns occur", Icons.Default.Category)
        )
    )
)

@Composable
fun AdminFeatureGroup(category: AdminFeatureCategory, onItemClick: (String) -> Unit) {
    Column {
        Text(
            text = category.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        category.features.forEach { feature ->
            AdminFeatureItem(feature = feature, onItemClick = onItemClick)
        }
    }
}

@Composable
fun AdminFeatureItem(feature: AdminNavFeature, onItemClick: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val itemBackgroundColor = Color.White.copy(alpha = 0.1f)

    val clickModifier = if (feature.route != null) {
        Modifier.clickable { onItemClick(feature.route) }
    } else {
        Modifier.clickable { isExpanded = !isExpanded }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(itemBackgroundColor)
            .then(clickModifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(feature.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(feature.title, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            if (feature.route == null) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand/Collapse",
                    tint = Color.White
                )
            }
        }
        AnimatedVisibility(visible = isExpanded) {
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            )
        }
    }
}
