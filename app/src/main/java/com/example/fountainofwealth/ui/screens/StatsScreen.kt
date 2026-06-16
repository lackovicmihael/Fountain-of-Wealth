package com.example.fountainofwealth.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fountainofwealth.ui.components.EmptyState
import com.example.fountainofwealth.ui.components.ScreenHeader
import com.example.fountainofwealth.viewmodel.MainUiState

private val ChartColors = listOf(
    Color(0xFF2563EB),
    Color(0xFFDC2626),
    Color(0xFFF59E0B),
    Color(0xFF7C3AED),
    Color(0xFF059669),
    Color(0xFFDB2777),
    Color(0xFF0891B2)
)

@Composable
fun StatsScreen(
    uiState: MainUiState,
    bottomBar: @Composable () -> Unit
) {
    val categoryData = uiState.expensesByCategory
    val total = categoryData.values.sum()

    Scaffold(bottomBar = bottomBar) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ScreenHeader(
                    title = "My Statistics",
                    subtitle = "Expense breakdown by category"
                )
            }

            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (total <= 0.0) {
                            EmptyState(
                                title = "No statistics yet",
                                message = "Add expenses to generate your spending chart.",
                                icon = Icons.Default.PieChart
                            )
                        } else {
                            ExpensePieChart(
                                categoryData = categoryData,
                                modifier = Modifier.size(220.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Total spent",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "€${"%.2f".format(total)}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }

            if (categoryData.isNotEmpty()) {
                itemsIndexed(categoryData.toList()) { index, item ->
                    val category = item.first
                    val amount = item.second
                    val percentage = if (total > 0.0) amount / total * 100.0 else 0.0
                    val color = ChartColors[index % ChartColors.size]

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(
                                        color = color,
                                        shape = MaterialTheme.shapes.small
                                    )
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = "${"%.1f".format(percentage)}% of total expenses",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "€${"%.2f".format(amount)}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpensePieChart(
    categoryData: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val total = categoryData.values.sum()
    var startAngle = -90f

    Canvas(modifier = modifier) {
        categoryData.entries.forEachIndexed { index, entry ->
            val sweepAngle = ((entry.value / total) * 360f).toFloat()

            drawArc(
                color = ChartColors[index % ChartColors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height)
            )

            startAngle += sweepAngle
        }
    }
}