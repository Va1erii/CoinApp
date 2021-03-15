package com.valerii.coinapp.repository

import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import com.valerii.coinapp.model.Quote
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.random.Random

/**
 * Generates mock rates for the specified source.
 * For free API access, we cannot change source, so it uses for mocking real response
 */
interface FakeCurrencyRateGenerator {
    fun calculate(
        source: String,
        currencies: List<Currency>
    ): Single<CurrencyRate>
}

class BaseFakeCurrencyRateGenerator : FakeCurrencyRateGenerator {
    override fun calculate(
        source: String,
        currencies: List<Currency>
    ): Single<CurrencyRate> {
        return Single.create<CurrencyRate> { emitter ->
            val random = Random
            val quotes: List<Quote> =
                currencies.asSequence()
                    .map { Quote("$source${it.name}", random.nextDouble().toString()) }
                    .toList()
            if (!emitter.isDisposed) {
                emitter.onSuccess(CurrencyRate(source, System.currentTimeMillis(), quotes))
            }
        }.subscribeOn(Schedulers.computation())
    }
}