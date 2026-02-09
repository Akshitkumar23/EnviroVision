package com.example.wastemanagement.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IncidentTypeSelector(
    selectedType: String,
    customType: String,
    onTypeSelected: (String) -> Unit,
    onCustomTypeChanged: (String) -> Unit
) {
    val categories = listOf("Uncollected Garbage", "Overflowing Bin", "Illegal Dumping", "Other")

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Category", style = MaterialTheme.typography.titleMedium)
        ScrollableTabRow(
            selectedTabIndex = if (selectedType in categories) categories.indexOf(selectedType) else categories.indexOf("Other"),
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp
        ) {
            categories.forEach { category ->
                Tab(
                    selected = selectedType == category,
                    onClick = { onTypeSelected(category) },
                    text = { Text(category) }
                )
            }
        }

        if (selectedType == "Other") {
            OutlinedTextField(
                value = customType,
                onValueChange = onCustomTypeChanged,
                label = { Text("Please specify") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}
