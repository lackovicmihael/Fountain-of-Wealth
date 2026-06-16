package com.example.fountainofwealth.data.model

data class CurrencyRate(
    val code: String,
    val value: Double
)

data class FrankfurterResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)