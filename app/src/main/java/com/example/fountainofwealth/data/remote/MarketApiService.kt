package com.example.fountainofwealth.data.remote

import com.example.fountainofwealth.data.model.FrankfurterResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {

    @GET("v1/latest")
    suspend fun getLatestRates(
        @Query("from") from: String = "EUR",
        @Query("to") to: String = "USD,GBP,CHF,BAM,AUD,CAD,JPY"
    ): FrankfurterResponse
}

interface CryptoApiService {

    @GET("api/v3/simple/price")
    suspend fun getCryptoPrices(
        @Query("ids") ids: String = "bitcoin,ethereum,solana,cardano,dogecoin,polkadot",
        @Query("vs_currencies") vsCurrencies: String = "eur"
    ): Map<String, Map<String, Double>>
}