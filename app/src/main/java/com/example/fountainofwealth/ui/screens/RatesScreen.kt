package com.example.fountainofwealth.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fountainofwealth.ui.components.EmptyState
import com.example.fountainofwealth.ui.components.LoadingBar
import com.example.fountainofwealth.ui.components.ScreenHeader
import com.example.fountainofwealth.viewmodel.MainUiState

@Composable
fun RatesScreen(
    uiState: MainUiState,
    onRefresh: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    Scaffold(bottomBar = bottomBar) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                ScreenHeader(
                    title = "Exchange rates",
                    subtitle = "EUR base currency",
                    action = {
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh rates")
                        }
                    }
                )
            }

            item(span = { GridItemSpan(2) }) {
                LoadingBar(visible = uiState.isRefreshing)
            }

            if (uiState.currencyRates.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    EmptyState(
                        title = "No rates available",
                        message = "Tap refresh to load exchange rates.",
                        icon = Icons.Default.CurrencyExchange
                    )
                }
            } else {
                items(uiState.currencyRates) { rate ->
                    ElevatedCard {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text("EUR → ${rate.code}") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                border = AssistChipDefaults.assistChipBorder(
                                    enabled = true,
                                    borderColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            Text(
                                text = "%.4f".format(rate.value),
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Text(
                                text = "Current exchange rate",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}