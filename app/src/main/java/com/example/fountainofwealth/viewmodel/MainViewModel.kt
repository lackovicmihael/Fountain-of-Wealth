package com.example.fountainofwealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fountainofwealth.data.firebase.FirebaseRepository
import com.example.fountainofwealth.data.model.CryptoRate
import com.example.fountainofwealth.data.model.CurrencyRate
import com.example.fountainofwealth.data.model.Transaction
import com.example.fountainofwealth.data.remote.MarketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoggedIn: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,

    val budget: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val totalExpenses: Double = 0.0,

    val transactions: List<Transaction> = emptyList(),
    val expensesByCategory: Map<String, Double> = emptyMap(),

    val currencyRates: List<CurrencyRate> = emptyList(),
    val cryptoRates: List<CryptoRate> = emptyList()
)

class MainViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()
    private val marketRepository = MarketRepository()

    private val _uiState = MutableStateFlow(
        MainUiState(
            isLoggedIn = firebaseRepository.currentUserId() != null
        )
    )

    val uiState: StateFlow<MainUiState> = _uiState

    init {
        if (_uiState.value.isLoggedIn) {
            refreshAll()
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true, errorMessage = null)
            }

            try {
                firebaseRepository.login(email, password)
                _uiState.update {
                    it.copy(isLoggedIn = true)
                }
                refreshAll()
                onSuccess()
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Login failed. Check your e-mail and password.")
                }
            }

            _uiState.update {
                it.copy(isSaving = false)
            }
        }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true, errorMessage = null)
            }

            try {
                firebaseRepository.register(email, password)
                _uiState.update {
                    it.copy(isLoggedIn = true)
                }
                refreshAll()
                onSuccess()
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Registration failed. Try another e-mail or password.")
                }
            }

            _uiState.update {
                it.copy(isSaving = false)
            }
        }
    }

    fun logout() {
        firebaseRepository.logout()
        _uiState.value = MainUiState(isLoggedIn = false)
    }

    fun refreshAll() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isRefreshing = true)
            }

            val budget = runCatching {
                firebaseRepository.getBudget()
            }.getOrDefault(0.0)

            val transactions = runCatching {
                firebaseRepository.getTransactions()
            }.getOrDefault(emptyList())

            val currencyRates = runCatching {
                marketRepository.getCurrencyRates()
            }.getOrDefault(emptyList())

            val cryptoRates = runCatching {
                marketRepository.getCryptoRates()
            }.getOrDefault(emptyList())

            updateMoneyState(
                budget = budget,
                transactions = transactions,
                currencyRates = currencyRates,
                cryptoRates = cryptoRates
            )

            _uiState.update {
                it.copy(isRefreshing = false)
            }
        }
    }

    fun updateBudget(input: String) {
        val newBudget = input.replace(",", ".").toDoubleOrNull()

        if (newBudget == null || newBudget < 0.0) {
            _uiState.update {
                it.copy(errorMessage = "Enter a valid budget amount.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true)
            }

            val success = runCatching {
                firebaseRepository.updateBudget(newBudget)
            }.isSuccess

            if (success) {
                updateMoneyState(
                    budget = newBudget,
                    transactions = _uiState.value.transactions,
                    currencyRates = _uiState.value.currencyRates,
                    cryptoRates = _uiState.value.cryptoRates
                )
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Budget could not be saved.")
                }
            }

            _uiState.update {
                it.copy(isSaving = false)
            }
        }
    }

    fun addExpense(title: String, amountInput: String, category: String) {
        val amount = amountInput.replace(",", ".").toDoubleOrNull()

        if (title.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter expense title.")
            }
            return
        }

        if (amount == null || amount <= 0.0) {
            _uiState.update {
                it.copy(errorMessage = "Enter a valid amount.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true)
            }

            val success = runCatching {
                firebaseRepository.addTransaction(
                    Transaction(
                        title = title.trim(),
                        amount = amount,
                        category = category,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }.isSuccess

            if (success) {
                refreshTransactionsOnly()
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Expense could not be saved.")
                }
            }

            _uiState.update {
                it.copy(isSaving = false)
            }
        }
    }

    fun deleteExpense(transactionId: String) {
        viewModelScope.launch {
            val success = runCatching {
                firebaseRepository.deleteTransaction(transactionId)
            }.isSuccess

            if (success) {
                refreshTransactionsOnly()
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Expense could not be deleted.")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    private suspend fun refreshTransactionsOnly() {
        val result = runCatching {
            firebaseRepository.getTransactions()
        }

        if (result.isFailure) {
            _uiState.update {
                it.copy(errorMessage = "Could not load transactions.")
            }
            return
        }

        val transactions = result.getOrDefault(emptyList())

        updateMoneyState(
            budget = _uiState.value.budget,
            transactions = transactions,
            currencyRates = _uiState.value.currencyRates,
            cryptoRates = _uiState.value.cryptoRates
        )
    }

    private fun updateMoneyState(
        budget: Double,
        transactions: List<Transaction>,
        currencyRates: List<CurrencyRate>,
        cryptoRates: List<CryptoRate>
    ) {
        val totalExpenses = transactions.sumOf {
            it.amount
        }

        val expensesByCategory = transactions
            .groupBy {
                it.category
            }
            .mapValues { entry ->
                entry.value.sumOf {
                    it.amount
                }
            }

        _uiState.update {
            it.copy(
                budget = budget,
                remainingBudget = budget - totalExpenses,
                totalExpenses = totalExpenses,
                transactions = transactions,
                expensesByCategory = expensesByCategory,
                currencyRates = currencyRates,
                cryptoRates = cryptoRates
            )
        }
    }
}