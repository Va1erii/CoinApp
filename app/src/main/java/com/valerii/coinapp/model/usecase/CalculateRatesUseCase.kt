package com.valerii.coinapp.model.usecase

import androidx.annotation.WorkerThread
import com.valerii.coinapp.model.Quote
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal

interface CalculateRatesUseCase {
    fun calculate(
        sourceValue: BigDecimal,
        baseRates: List<Quote>
    ): Observable<List<Quote>>
}

class BaseCalculateRatesUseCase : CalculateRatesUseCase {
    @WorkerThread
    override fun calculate(
        sourceValue: BigDecimal,
        baseRates: List<Quote>
    ): Observable<List<Quote>> {
        return Observable.create<List<Quote>> { emitter ->
            emitter.onNext(
                baseRates.asSequence()
                    .map { it.copy(rate = sourceValue.multiply((it.rate.toBigDecimal())).toEngineeringString()) }
                    .toList()
            )
        }
            .subscribeOn(Schedulers.computation())
    }
}