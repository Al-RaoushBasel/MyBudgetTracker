package com.example.my_budget_tracker.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object for creating and managing the Retrofit instance for API calls.
 * Automatically adds the API key to all requests.
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.exchangeratesapi.io/v1/" // Base URL for the API
    private const val API_KEY = "351f29810ea92090c3e63c4657c0cf7e" // API key for authentication

    // OkHttpClient with an interceptor to add the API key to every request
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url

            // Append the API key as a query parameter
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("access_key", API_KEY)
                .build()

            val request = original.newBuilder().url(url).build()
            chain.proceed(request)
        }
        .build()

    /**
     * Lazy-initialized Retrofit instance for the Currency API service.
     */
    val instance: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
}
