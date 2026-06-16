package com.example.fountainofwealth.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fountainofwealth.data.model.Transaction
import com.example.fountainofwealth.ui.components.EmptyState
import com.example.fountainofwealth.ui.components.LoadingBar
import com.example.fountainofwealth.ui.components.ScreenHeader
import com.example.fountainofwealth.viewmodel.MainUiState

@Composable
fun HomeScreen(
    uiState: MainUiState,
    onAddExpense: (String, String, String) -> Unit,
    onDeleteExpense: (String) -> Unit,
    onUpdateBudget: (String) -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }

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
                    title = "Fountain of Wealth",
                    subtitle = "Track your money with clarity",
                    action = {
                        Row {
                            IconButton(onClick = onRefresh) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }

                            IconButton(onClick = onLogout) {
                                Icon(Icons.Default.Logout, contentDescription = "Logout")
                            }
                        }
                    }
                )
            }

            item {
                LoadingBar(visible = uiState.isRefreshing)
            }

            item {
                BudgetCard(
                    budget = uiState.budget,
                    remainingBudget = uiState.remainingBudget,
                    totalExpenses = uiState.totalExpenses,
                    onAddExpenseClick = {
                        showExpenseDialog = true
                    },
                    onAddBudgetClick = {
                        showBudgetDialog = true
                    }
                )
            }

            item {
                Text(
                    text = "Recent transactions",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (uiState.transactions.isEmpty()) {
                item {
                    EmptyState(
                        title = "No expenses yet",
                        message = "Add your first expense to start tracking your budget.",
                        icon = Icons.Default.AccountBalanceWallet
                    )
                }
            } else {
                items(uiState.transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = {
                            onDeleteExpense(transaction.id)
                        }
                    )
                }
            }
        }
    }

    if (showExpenseDialog) {
        AddExpenseDialog(
            onDismiss = {
                showExpenseDialog = false
            },
            onConfirm = { title, amount, category ->
                onAddExpense(title, amount, category)
                showExpenseDialog = false
            }
        )
    }

    if (showBudgetDialog) {
        AddBudgetDialog(
            currentBudget = uiState.budget,
            onDismiss = {
                showBudgetDialog = false
            },
            onConfirm = { budget ->
                onUpdateBudget(budget)
                showBudgetDialog = false
            }
        )
    }
}

@Composable
private fun BudgetCard(
    budget: Double,
    remainingBudget: Double,
    totalExpenses: Double,
    onAddExpenseClick: () -> Unit,
    onAddBudgetClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Remaining budget",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "€${"%.2f".format(remainingBudget)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallMoneyCard(
                    title = "Budget",
                    value = "€${"%.2f".format(budget)}",
                    modifier = Modifier.weight(1f)
                )

                SmallMoneyCard(
                    title = "Expenses",
                    value = "€${"%.2f".format(totalExpenses)}",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddExpenseClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(text = "Add expense")
                }

                FilledTonalButton(
                    onClick = onAddBudgetClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Text(text = "Add budget")
                }
            }
        }
    }
}

@Composable
private fun SmallMoneyCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "-€${"%.2f".format(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete expense"
                )
            }
        }
    }
}

@Composable
private fun AddBudgetDialog(
    currentBudget: Double,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var budget by remember {
        mutableStateOf(
            if (currentBudget > 0.0) "%.2f".format(currentBudget) else ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add budget")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter your starting budget, salary, or current available balance."
                )

                OutlinedTextField(
                    value = budget,
                    onValueChange = {
                        budget = it
                    },
                    label = {
                        Text("Budget amount")
                    },
                    singleLine = true,
                    prefix = {
                        Text("€")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(budget)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    val categories = listOf("Food", "Transport", "Fun", "Bills", "Other")

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add expense")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text("Title")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    label = {
                        Text("Amount")
                    },
                    singleLine = true,
                    prefix = {
                        Text("€")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    categories.forEach { item ->
                        FilledTonalButton(
                            onClick = {
                                category = item
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (category == item) "✓ $item" else item
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(title, amount, category)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}