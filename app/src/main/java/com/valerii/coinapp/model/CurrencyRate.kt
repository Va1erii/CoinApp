package com.valerii.coinapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyRate(
    @PrimaryKey val source: String,
    val timestamp: Long,
    val quotes: List<Quote>
) {
    companion object {
        val Empty = CurrencyRate("", 0, emptyList())
    }
}