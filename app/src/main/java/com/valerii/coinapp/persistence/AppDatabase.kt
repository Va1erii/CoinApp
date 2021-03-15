package com.valerii.coinapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import com.valerii.coinapp.persistence.converter.QuoteConverter

@Database(entities = [Currency::class, CurrencyRate::class], version = 1, exportSchema = true)
@TypeConverters(value = [QuoteConverter::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun currencyRateDao(): CurrencyRateDao
}