package com.example.my_budget_tracker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.my_budget_tracker.network.RetrofitClient

object CurrencyManager {
    private var rates: Map<String, Double>? = null
    var selectedCurrency: String = "EUR"
    var conversionRate: Double = 1.0
    private lateinit var sharedPreferences: SharedPreferences

    // Initialize shared preferences
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
        selectedCurrency = sharedPreferences.getString("selected_currency", "EUR") ?: "EUR"
    }

    // Fetch exchange rates
    suspend fun fetchExchangeRates(forceRefresh: Boolean = false) {
        if (rates == null || forceRefresh) {
            val response = RetrofitClient.instance.getExchangeRates(
                accessKey = "351f29810ea92090c3e63c4657c0cf7e",
                base = "EUR",
                symbols = "USD,GBP,HUF"
            )
            rates = response.rates.toMutableMap().apply {
                this["EUR"] = 1.0 // Add base currency
            }
            println("Fetched Rates: $rates") // Log fetched rates
        }
    }


    // Set the selected currency and store it in shared preferences
    fun setCurrency(currency: String) {
        if (rates?.containsKey(currency) == true) {
            selectedCurrency = currency
            conversionRate = rates!![currency] ?: 1.0
            sharedPreferences.edit().putString("selected_currency", currency).apply()
        }
    }

    // Format the amount for display
    fun formatAmount(amount: Double): String {
        return "%.2f $selectedCurrency".format(amount)
    }

    // Convert the amount from one currency to another
    fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) {
            return amount
        }

        if (rates.isNullOrEmpty() || !rates!!.containsKey(fromCurrency) || !rates!!.containsKey(toCurrency)) {
            return amount // Fallback to original amount
        }

        val fromRate = rates!![fromCurrency] ?: 1.0
        val toRate = rates!![toCurrency] ?: 1.0

        // Debugging: Print rates and conversion values
        println("Converting $amount from $fromCurrency to $toCurrency")
        println("From rate: $fromRate, To rate: $toRate")

        return amount * (toRate / fromRate)
    }
}


