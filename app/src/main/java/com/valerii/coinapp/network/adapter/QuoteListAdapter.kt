package com.valerii.coinapp.network.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.valerii.coinapp.model.Quote

class QuoteListAdapter : JsonAdapter<List<Quote>>() {

    override fun fromJson(reader: JsonReader): List<Quote> {
        val quotes = ArrayList<Quote>()
        reader.beginObject()
        while (reader.hasNext() && reader.peek() == JsonReader.Token.NAME) {
            val name = reader.nextName()
            val value = reader.nextString()
            quotes.add(Quote(name, value))
        }
        reader.endObject()
        return quotes
    }

    override fun toJson(writer: JsonWriter, value: List<Quote>?) {
        value?.let {
            writer.beginObject()
            value.forEach {
                writer.name(it.quote)
                writer.jsonValue(it.rate)
            }
            writer.endObject()
        }
    }
}