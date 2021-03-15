package com.valerii.coinapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.valerii.coinapp.model.Quote

@JsonClass(generateAdapter = true)
data class CurrencyRatesResponse(
    @field:Json(name = "success") val success: Boolean,
    @field:Json(name = "timestamp") val timestamp: Long,
    @field:Json(name = "source") val source: String,
    @field:Json(name = "quotes") val quotes: List<Quote>
)