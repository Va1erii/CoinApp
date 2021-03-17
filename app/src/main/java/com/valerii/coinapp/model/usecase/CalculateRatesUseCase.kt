package com.valerii.coinapp.model.usecase

import androidx.annotation.WorkerThread
import com.valerii.coinapp.model.Quote
import com.valerii.coinapp.utils.BigDecimalValueConverter
import com.valerii.coinapp.utils.BigDecimalValueConverter.ConvertResult.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

interface CalculateRatesUseCase {
    /**
     * Calculate currency rates with the specified source value
     * @param sourceValue Source value
     * @param baseRates Source rates: sourceValue == 1
     */
    fun calculate(
        sourceValue: BigDecimal,
        baseRates: List<Quote>,
    ): Observable<List<Quote>>
}

class BaseCalculateRatesUseCase @Inject constructor(
    private val valueConverter: BigDecimalValueConverter,
) : CalculateRatesUseCase {
    @WorkerThread
    override fun calculate(
        sourceValue: BigDecimal,
        baseRates: List<Quote>,
    ): Observable<List<Quote>> {
        return Observable.create<List<Quote>> { emitter ->
            emitter.onNext(
                baseRates.asSequence()
                    .map {
                        val quoteValue = valueConverter.convert(it.rate)
                        val rateValue = when (quoteValue) {
                            Empty -> "-"
                            is Success -> valueConverter.convert(
                                quoteValue.value.multiply(sourceValue)
                            )
                            is Error -> "NaN"
                        }
                        it.copy(rate = rateValue)
                    }
                    .toList()
            )
        }.subscribeOn(Schedulers.computation())
    }
}