package com.valerii.coinapp.persistence

import com.valerii.coinapp.model.Currency

class MockCurrencyDao(
    private val onSaved: () -> Unit,
) : CurrencyDao {
    var result: List<Currency> = emptyList()

    override fun insertCurrencyList(currencyList: List<Currency>) {
        onSaved()
    }

    override fun getCurrencyList(): List<Currency> {
        return result
    }
}