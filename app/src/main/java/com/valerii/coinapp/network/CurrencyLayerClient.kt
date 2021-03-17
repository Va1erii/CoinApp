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

interface CurrencyLayerClient {
    fun fetchCurrencyList(): Single<ClientResponse<List<Currency>>>
    fun fetchCurrencyRates(source: String): Single<ClientResponse<CurrencyRate>>
}

class BaseCurrencyLayerClient @Inject constructor(
    private val currencyLayerService: CurrencyLayerService,
    private val currencyListResponseMapper: CurrencyListResponseMapper,
    private val currencyRatesResponseMapper: CurrencyRatesResponseMapper,
) : CurrencyLayerClient {
    /**
     * Return available currencies
     */
    @WorkerThread
    override fun fetchCurrencyList(): Single<ClientResponse<List<Currency>>> {
        return Single.create<ClientResponse<List<Currency>>> { emitter ->
            val response = currencyLayerService.fetchCurrencyList().execute()
            if (!emitter.isDisposed) {
                emitter.onSuccess(currencyListResponseMapper.map(response))
            }
        }.subscribeOn(Schedulers.io())
    }

    /**
     * Return currency rates containing all available or specified currency pairs with their respective exchange rate values
     * <br>
     * Note: Default source is USD. For free API access, we cannot change source.
     * @param source Currency to which all exchange rates are relative
     */
    @WorkerThread
    override fun fetchCurrencyRates(source: String): Single<ClientResponse<CurrencyRate>> {
        return Single.create<ClientResponse<CurrencyRate>> { emitter ->
            val response = currencyLayerService.fetchCurrencyRates(source).execute()
            if (!emitter.isDisposed) {
                emitter.onSuccess(currencyRatesResponseMapper.map(response))
            }
        }.subscribeOn(Schedulers.io())
    }
}