package com.example.wastemanagement.screens.feature.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wastemanagement.R

@Composable
fun SmartBinsMapPreview(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Text(
                "Smart Bins Near You",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
            Box(modifier = Modifier.height(150.dp)) {
                // Placeholder for a map preview
                Image(
                    painter = painterResource(id = R.drawable.map_placeholder),
                    contentDescription = "Map preview showing smart bin locations",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(
                onClick = { /* TODO: Navigate to full Smart Bin Map screen */ },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(12.dp)
            ) {
                Text("See Details")
            }
        }
    }
}
