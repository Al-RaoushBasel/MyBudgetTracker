package com.example.my_budget_tracker.network

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for fetching currency exchange rates.
 */
interface CurrencyApiService {

    /**
     * Fetches the latest exchange rates from the API.
     *
     * @param accessKey The API access key for authentication.
     * @param base The base currency (e.g., "EUR").
     * @param symbols A comma-separated list of target currencies (e.g., "USD,GBP,HUF").
     * @return An [ExchangeRatesResponse] containing the exchange rate data.
     */
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): ExchangeRatesResponse
}
