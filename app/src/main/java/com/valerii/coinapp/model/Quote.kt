package com.valerii.coinapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Quote(
    @field:Json(name = "name") val quote: String,
    @field:Json(name = "value") val rate: String
)