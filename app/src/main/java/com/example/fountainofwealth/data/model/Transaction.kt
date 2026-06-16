package com.example.fountainofwealth.data.model

data class Transaction(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis()
)