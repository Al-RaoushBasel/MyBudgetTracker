package com.example.my_budget_tracker.data

import com.example.my_budget_tracker.network.RetrofitClient

object CurrencyManager {
    private var rates: Map<String, Double>? = null
    var selectedCurrency: String = "HUF" // Default currency
    var conversionRate: Double = 1.0 // Default rate (1 EUR = 1 EUR)

    // Method to fetch exchange rates
    suspend fun fetchExchangeRates() {
        val response = RetrofitClient.instance.getExchangeRates(
            accessKey = "9452757950ecb43d5cae412f5ee3fcfb",
            base = "EUR",
            symbols = "USD,GBP,HUF"
        )
        rates = response.rates
    }



    // Method to set selected currency
    fun setCurrency(currency: String) {
        selectedCurrency = currency
        conversionRate = rates?.get(currency) ?: 1.0
    }

    // Method to convert and format an amount
    fun formatAmount(amount: Double): String {
        val convertedAmount = amount * conversionRate
        return "%.2f $selectedCurrency".format(convertedAmount)
    }

    // Method to manually convert between currencies
    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (rates == null) throw IllegalStateException("Rates not fetched yet")
        val fromRate = rates!![fromCurrency] ?: throw IllegalArgumentException("Invalid from currency")
        val toRate = rates!![toCurrency] ?: throw IllegalArgumentException("Invalid to currency")
        return amount * (toRate / fromRate)
    }

    fun getRates(): Map<String, Double>? {
        return rates
    }
}
