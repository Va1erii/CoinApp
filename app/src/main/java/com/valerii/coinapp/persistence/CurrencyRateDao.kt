package com.valerii.coinapp.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valerii.coinapp.model.CurrencyRate

@Dao
interface CurrencyRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencyRate(currencyRate: CurrencyRate)

    @Query("SELECT * FROM CurrencyRate WHERE source = :source_")
    fun getCurrencyRate(source_: String): CurrencyRate?
}