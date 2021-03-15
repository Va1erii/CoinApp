package com.valerii.coinapp.network.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.valerii.coinapp.model.Currency

class CurrencyListAdapter : JsonAdapter<List<Currency>>() {

    override fun fromJson(reader: JsonReader): List<Currency> {
        val currencies = ArrayList<Currency>()
        reader.beginObject()
        while (reader.hasNext() && reader.peek() == JsonReader.Token.NAME) {
            val name = reader.nextName()
            val description = reader.nextString()
            currencies.add(Currency(name, description))
        }
        reader.endObject()
        return currencies
    }

    override fun toJson(writer: JsonWriter, value: List<Currency>?) {
        value?.let {
            writer.beginObject()
            value.forEach {
                writer.name(it.name)
                writer.jsonValue(it.description)
            }
            writer.endObject()
        }
    }
}