package com.valerii.coinapp.persistence

import com.valerii.coinapp.model.CurrencyRate

class MockCurrencyRateDao(
    private val onSaved: () -> Unit,
) : CurrencyRateDao {
    var result: CurrencyRate? = null

    override fun insertCurrencyRate(currencyRate: CurrencyRate) {
        onSaved()
    }

    override fun getCurrencyRate(source_: String): CurrencyRate? {
        return result
    }
}