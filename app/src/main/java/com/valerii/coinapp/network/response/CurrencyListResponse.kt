package com.valerii.coinapp.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.valerii.coinapp.model.Currency

@JsonClass(generateAdapter = true)
data class CurrencyListResponse(
    @field:Json(name = "success") val success: Boolean,
    @field:Json(name = "currencies") val currencies: List<Currency>
)