package com.example.wastemanagement.screens.feature.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentCategoryFilter() {
    val categories = listOf("Solid Waste", "Air", "Water", "Noise", "Hazardous", "Others")
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
        items(categories) {
            FilterChip(
                modifier = Modifier.padding(horizontal = 4.dp),
                selected = it == selectedCategory,
                onClick = { selectedCategory = if (selectedCategory == it) null else it },
                label = { Text(it) },
                leadingIcon = if (it == selectedCategory) {
                    { Icon(Icons.Default.Check, contentDescription = "Selected") }
                } else {
                    null
                }
            )
        }
    }
}
