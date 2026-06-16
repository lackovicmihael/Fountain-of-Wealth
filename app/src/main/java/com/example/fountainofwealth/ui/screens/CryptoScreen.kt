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
import androidx.compose.material.icons.filled.Paid
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
fun CryptoScreen(
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
                    title = "Crypto market",
                    subtitle = "Prices shown in EUR",
                    action = {
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh crypto")
                        }
                    }
                )
            }

            item(span = { GridItemSpan(2) }) {
                LoadingBar(visible = uiState.isRefreshing)
            }

            if (uiState.cryptoRates.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    EmptyState(
                        title = "No prices available",
                        message = "Tap refresh to load crypto prices.",
                        icon = Icons.Default.Paid
                    )
                }
            } else {
                items(uiState.cryptoRates) { crypto ->
                    ElevatedCard {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text(crypto.symbol.uppercase()) },
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
                                text = crypto.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "€${"%.2f".format(crypto.priceEur)}",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Text(
                                text = "Live market price",
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