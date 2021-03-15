package com.valerii.coinapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Contains exchange rate value, consisting of the currency pair and its respective conversion rate
 *
 * I decided to not divide quote to the source and rates currency, because the quote format could
 * change and it would be inconvenient to map to the certain currency.
 * <br>
 * For example: USD/EUR (default USDEUR)
 */
@JsonClass(generateAdapter = true)
data class Quote(
    @field:Json(name = "name") val quote: String,
    @field:Json(name = "value") val rate: String
)