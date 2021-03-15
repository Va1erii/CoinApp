package com.valerii.coinapp.persistence.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.valerii.coinapp.model.Quote
import javax.inject.Inject

@ProvidedTypeConverter
class QuoteConverter @Inject constructor(
    private val moshi: Moshi
) {
    @TypeConverter
    fun fromString(value: String): List<Quote>? {
        val listType = Types.newParameterizedType(List::class.java, Quote::class.java)
        val adapter: JsonAdapter<List<Quote>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromQuote(quotes: List<Quote>): String {
        val listType = Types.newParameterizedType(List::class.java, Quote::class.java)
        val adapter: JsonAdapter<List<Quote>> = moshi.adapter(listType)
        return adapter.toJson(quotes)
    }
}