package com.valerii.coinapp.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valerii.coinapp.model.Currency

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencyList(currencyList: List<Currency>)

    @Query("SELECT * FROM Currency")
    fun getCurrencyList(): List<Currency>
}