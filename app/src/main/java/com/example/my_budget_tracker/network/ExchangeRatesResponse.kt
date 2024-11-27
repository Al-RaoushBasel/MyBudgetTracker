package com.example.my_budget_tracker.network

data class ExchangeRatesResponse(
    val success: Boolean,
    val base: String,
    val rates: Map<String, Double>,
    val date: String
)
