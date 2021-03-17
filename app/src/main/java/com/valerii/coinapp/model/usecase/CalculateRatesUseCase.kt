package com.valerii.coinapp.model.usecase

import androidx.annotation.WorkerThread
import com.valerii.coinapp.model.Quote
import com.valerii.coinapp.utils.ValueConverter
import com.valerii.coinapp.utils.ValueConverter.ConvertResult
import com.valerii.coinapp.utils.ValueFormatter
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
    private val valueConverter: ValueConverter<BigDecimal>,
    private val valueFormatter: ValueFormatter<BigDecimal>,
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
                            is ConvertResult.Empty -> ValueConverter.NaN
                            is ConvertResult.Success -> {
                                valueConverter.convertToString(
                                    quoteValue.value.multiply(sourceValue),
                                    valueFormatter
                                )
                            }
                            is ConvertResult.Error -> ValueConverter.NaN
                        }
                        it.copy(rate = rateValue)
                    }
                    .toList()
            )
            emitter.onComplete()
        }.subscribeOn(Schedulers.computation())
    }
}