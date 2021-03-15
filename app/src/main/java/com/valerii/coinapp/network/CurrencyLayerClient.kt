package com.valerii.coinapp.network

import androidx.annotation.WorkerThread
import com.valerii.coinapp.mapper.CurrencyListResponseMapper
import com.valerii.coinapp.mapper.CurrencyRatesResponseMapper
import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CurrencyLayerClient @Inject constructor(
    private val currencyLayerService: CurrencyLayerService,
    private val currencyListResponseMapper: CurrencyListResponseMapper,
    private val currencyRatesResponseMapper: CurrencyRatesResponseMapper
) {
    @WorkerThread
    fun fetchCurrencyList(): Single<ClientResponse<List<Currency>>> {
        return Single.create<ClientResponse<List<Currency>>> { emitter ->
            val response = currencyLayerService.fetchCurrencyList().execute()
            if (!emitter.isDisposed) {
                emitter.onSuccess(currencyListResponseMapper.map(response))
            }
        }.subscribeOn(Schedulers.io())
    }

    @WorkerThread
    fun fetchCurrencyRates(source: String): Single<ClientResponse<CurrencyRate>> {
        return Single.create<ClientResponse<CurrencyRate>> { emitter ->
            val response = currencyLayerService.fetchCurrencyRates("USD").execute()
            if (!emitter.isDisposed) {
                emitter.onSuccess(currencyRatesResponseMapper.map(response))
            }
        }.subscribeOn(Schedulers.io())
    }
}