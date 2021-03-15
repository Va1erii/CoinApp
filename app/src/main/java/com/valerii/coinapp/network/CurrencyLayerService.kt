package com.valerii.coinapp.network

import com.valerii.coinapp.network.response.CurrencyListResponse
import com.valerii.coinapp.network.response.CurrencyRatesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyLayerService {
    @GET("list")
    fun fetchCurrencyList(): Call<CurrencyListResponse>

    @GET("live")
    fun fetchCurrencyRates(
        @Query("source") source: String
    ): Call<CurrencyRatesResponse>
}