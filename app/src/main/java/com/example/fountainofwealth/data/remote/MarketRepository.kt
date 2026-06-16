package com.example.fountainofwealth.data.remote

import com.example.fountainofwealth.data.model.CryptoRate
import com.example.fountainofwealth.data.model.CurrencyRate
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MarketRepository {

    private val currencyApi: CurrencyApiService = Retrofit.Builder()
        .baseUrl("https://api.frankfurter.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CurrencyApiService::class.java)

    private val cryptoApi: CryptoApiService = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CryptoApiService::class.java)

    suspend fun getCurrencyRates(): List<CurrencyRate> {
        val response = currencyApi.getLatestRates()

        val preferredOrder = listOf("USD", "GBP", "CHF", "BAM", "AUD", "CAD", "JPY")

        return preferredOrder.mapNotNull { code ->
            response.rates[code]?.let { value ->
                CurrencyRate(
                    code = code,
                    value = value
                )
            }
        }
    }

    suspend fun getCryptoRates(): List<CryptoRate> {
        val response = cryptoApi.getCryptoPrices()

        return listOf(
            CryptoRate(
                name = "Bitcoin",
                symbol = "BTC",
                priceEur = response["bitcoin"]?.get("eur") ?: 0.0
            ),
            CryptoRate(
                name = "Ethereum",
                symbol = "ETH",
                priceEur = response["ethereum"]?.get("eur") ?: 0.0
            ),
            CryptoRate(
                name = "Solana",
                symbol = "SOL",
                priceEur = response["solana"]?.get("eur") ?: 0.0
            ),
            CryptoRate(
                name = "Cardano",
                symbol = "ADA",
                priceEur = response["cardano"]?.get("eur") ?: 0.0
            ),
            CryptoRate(
                name = "Dogecoin",
                symbol = "DOGE",
                priceEur = response["dogecoin"]?.get("eur") ?: 0.0
            ),
            CryptoRate(
                name = "Polkadot",
                symbol = "DOT",
                priceEur = response["polkadot"]?.get("eur") ?: 0.0
            )
        )
    }
}