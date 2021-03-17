package com.valerii.coinapp.repository

import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import com.valerii.coinapp.model.Quote
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MockFakeCurrencyRateGenerator : FakeCurrencyRateGenerator {
    override fun calculate(source: String, currencies: List<Currency>): Single<CurrencyRate> {
        return Single.create<CurrencyRate> { emitter ->
            val quotes: List<Quote> =
                currencies.asSequence()
                    .map { Quote("$source${it.name}", "1") }
                    .toList()
            if (!emitter.isDisposed) {
                emitter.onSuccess(CurrencyRate(source, System.currentTimeMillis(), quotes))
            }
        }.subscribeOn(Schedulers.computation())
    }
}