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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun AppDrawer(onClose: () -> Unit, onItemClick: (String) -> Unit) {
    ModalDrawerSheet(drawerContainerColor = Color(0xFF1A237E).copy(alpha = 0.85f)) {
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
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        tint = Color.White
                    )
                    Column {
                        Text("Akshit", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Joined Member", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close Drawer", tint = Color.White)
                }
            }

            // Feature List
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(citizenFeatures) { category ->
                    FeatureGroup(category = category, onItemClick = onItemClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

data class NavFeature(val title: String, val description: String, val icon: ImageVector, val route: String? = null)

data class FeatureCategory(val title: String, val features: List<NavFeature>)

val citizenFeatures = listOf(
    FeatureCategory(
        title = "Core Functions",
        features = listOf(
            NavFeature("Dashboard", "Real-Time Tracking: Live status updates of complaint resolution with ETA notifications", Icons.Default.Dashboard, "citizen_dashboard"),
            NavFeature("Reward Dashboard", "Gamification with points, leaderboard, and eco-rewards for active participation", Icons.Default.EmojiEvents),
            NavFeature("Events & Drives", "View and join community cleanups and plantation drives", Icons.Default.Groups, "events_screen"),
            NavFeature("Events Calendar", "View upcoming and completed events in a calendar view", Icons.Default.CalendarMonth)
        )
    ),
    FeatureCategory(
        title = "AI-Powered Features",
        features = listOf(
            NavFeature("Waste Classification Guide", "AI-powered camera integration to identify waste types and suggest disposal methods", Icons.Default.CameraAlt),
            NavFeature("Predictive Collection Alerts", "Notifications about scheduled waste collection in user's area", Icons.Default.NotificationsActive),
            NavFeature("Personalized Sustainability Tips", "AI-driven recommendations based on user's waste generation patterns", Icons.Default.Lightbulb),
            NavFeature("Voice Assistant Integration", "Multi-language voice commands for complaint filing (supporting regional languages)", Icons.Default.RecordVoiceOver)
        )
    ),
    FeatureCategory(
        title = "Advanced Services",
        features = listOf(
            NavFeature("Carbon Footprint Tracker", "Personal environmental impact measurement with improvement suggestions", Icons.Default.Co2),
            NavFeature("Digital Payment Integration", "For waste management services and eco-friendly product purchases", Icons.Default.Payment),
            NavFeature("Emergency Pollution Alerts", "Real-time air quality and pollution level notifications", Icons.Default.Warning)
        )
    )
)

@Composable
fun FeatureGroup(category: FeatureCategory, onItemClick: (String) -> Unit) {
    Column {
        Text(
            text = category.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        category.features.forEach { feature ->
            FeatureItem(feature = feature, onItemClick = onItemClick)
        }
    }
}

@Composable
fun FeatureItem(feature: NavFeature, onItemClick: (String) -> Unit) {
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
            if (feature.route == null) { // Show expand icon only if it's not a direct navigation item
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
