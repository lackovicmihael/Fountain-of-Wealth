package com.example.fountainofwealth.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HelpScreen(
    bottomBar: @Composable () -> Unit
) {
    val faqItems = listOf(
        "How do I add an expense?" to "On Home, tap Add expense, then enter a title, amount and category.",
        "Where are my expenses stored?" to "Expenses are stored in Firebase Firestore under your user account.",
        "Where do exchange rates come from?" to "Current exchange rates are loaded from the Frankfurter API.",
        "Where do crypto prices come from?" to "BTC, ETH, SOL and ADA prices are loaded from CoinGecko.",
        "What are notifications for?" to "A daily reminder helps you record expenses consistently.",
        "How do I view statistics?" to "The Statistics screen shows spending grouped by category."
    )

    Scaffold(
        bottomBar = bottomBar
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Help",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text(
                    text = "FAQ",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            faqItems.forEachIndexed { index, item ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Q${index + 1}: ${item.first}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = item.second,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}