package com.example.my_budget_tracker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.my_budget_tracker.network.RetrofitClient

/**
 * Singleton object for managing currency exchange rates and conversions.
 */
object CurrencyManager {
    private var rates: Map<String, Double>? = null // Cached exchange rates
    var selectedCurrency: String = "EUR" // Default selected currency
    var conversionRate: Double = 1.0 // Conversion rate for the selected currency
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences for persistence

    /**
     * Initialize shared preferences and retrieve the previously selected currency.
     */
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
        selectedCurrency = sharedPreferences.getString("selected_currency", "EUR") ?: "EUR"
    }

    /**
     * Fetch exchange rates from the API.
     * @param forceRefresh Force the refresh of rates even if they are already cached.
     */
    suspend fun fetchExchangeRates(forceRefresh: Boolean = false) {
        if (rates == null || forceRefresh) {
            val response = RetrofitClient.instance.getExchangeRates(
                accessKey = "351f29810ea92090c3e63c4657c0cf7e",
                base = "EUR",
                symbols = "USD,GBP,HUF"
            )
            rates = response.rates.toMutableMap().apply {
                this["EUR"] = 1.0 // Ensure base currency is included
            }
            println("Fetched Rates: $rates") // Log fetched rates for debugging
        }
    }

    /**
     * Set the selected currency and save it in shared preferences.
     * Updates the conversion rate for the selected currency.
     * @param currency The selected currency (e.g., USD, GBP).
     */
    fun setCurrency(currency: String) {
        if (rates?.containsKey(currency) == true) {
            selectedCurrency = currency
            conversionRate = rates!![currency] ?: 1.0
            sharedPreferences.edit().putString("selected_currency", currency).apply()
        }
    }

    /**
     * Format an amount in the selected currency.
     * @param amount The amount to format.
     * @return Formatted string with the currency symbol.
     */
    fun formatAmount(amount: Double): String {
        return "%.2f $selectedCurrency".format(amount)
    }

    /**
     * Convert an amount between two currencies using the cached rates.
     * @param amount The amount to convert.
     * @param fromCurrency The currency to convert from.
     * @param toCurrency The currency to convert to.
     * @return The converted amount.
     */
    fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) {
            return amount // No conversion needed
        }

        if (rates.isNullOrEmpty() || !rates!!.containsKey(fromCurrency) || !rates!!.containsKey(toCurrency)) {
            return amount // Fallback to the original amount if rates are unavailable
        }

        val fromRate = rates!![fromCurrency] ?: 1.0
        val toRate = rates!![toCurrency] ?: 1.0

        // Debugging: Print conversion details
        println("Converting $amount from $fromCurrency to $toCurrency")
        println("From rate: $fromRate, To rate: $toRate")

        return amount * (toRate / fromRate)
    }
}
