package com.example.my_budget_tracker.network

/**
 * Data class representing the response from the currency exchange rates API.
 *
 * @property success Indicates whether the API call was successful.
 * @property base The base currency used for the exchange rates.
 * @property rates A map containing currency codes as keys and their respective exchange rates as values.
 * @property date The date of the exchange rates.
 */

data class ExchangeRatesResponse(
    val success: Boolean, // API success status
    val base: String, // Base currency ("EUR")
    val rates: Map<String, Double>, // Exchange rates mapped to currency codes
    val date: String // Date of the exchange rates
)
