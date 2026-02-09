package com.example.wastemanagement.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Metric(val title: String, val value: String, val color: Color)

@Composable
fun MetricCards(totalIncidents: Int, openIssues: Int, resolvedIncidents: Int, totalBins: Int) {
    val metrics = listOf(
        Metric("Total Incidents", totalIncidents.toString(), Color(0xFF2E7D32)),
        Metric("Open Issues", openIssues.toString(), Color(0xFFFFB300)),
        Metric("Resolved", resolvedIncidents.toString(), Color.Gray),
        Metric("Nearby Bins", totalBins.toString(), Color.Blue) // Now using real data
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(metrics) { metric ->
            MetricCardItem(metric)
        }
    }
}

@Composable
fun MetricCardItem(metric: Metric) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = metric.color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(metric.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(metric.value, style = MaterialTheme.typography.headlineLarge, color = Color.White)
        }
    }
}
