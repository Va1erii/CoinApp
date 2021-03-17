package com.valerii.coinapp.repository

import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import com.valerii.coinapp.network.CurrencyLayerClient
import com.valerii.coinapp.persistence.CurrencyDao
import com.valerii.coinapp.persistence.CurrencyRateDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val currencyLayerClient: CurrencyLayerClient,
    private val fakeCurrencyRateGenerator: FakeCurrencyRateGenerator,
    private val currencyDao: CurrencyDao,
    private val currencyRateDao: CurrencyRateDao,
) {
    companion object {
        private val RATE_UPDATE_TIME = TimeUnit.MINUTES.toMillis(30)
    }

    private val currencyList: BehaviorSubject<ClientResponse<List<Currency>>> =
        BehaviorSubject.create()
    private val currencyRates: BehaviorSubject<ClientResponse<CurrencyRate>> =
        BehaviorSubject.create()

    fun fetchCurrencyList(): Observable<ClientResponse<List<Currency>>> {
        return if (currencyList.hasValue()) {
            currencyList
        } else {
            // Fetch currencies from the database
            fetchCurrenciesFromDatabase()
                .subscribeOn(Schedulers.io())
                .flatMapObservable {
                    if (it.isEmpty()) {
                        // Fetch currencies from the server and save to the database
                        fetchCurrenciesFromServer()
                            .flatMapCompletable { saveCurrencies(it) }
                            .andThen(currencyList)
                    } else {
                        currencyList.onNext(ClientResponse.Success(it))
                        currencyList
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    private fun fetchCurrenciesFromDatabase(): Single<List<Currency>> {
        return Single.create { emitter ->
            val currencies = currencyDao.getCurrencyList()
            if (!emitter.isDisposed) emitter.onSuccess(currencies)
        }
    }

    private fun fetchCurrenciesFromServer(): Single<ClientResponse<List<Currency>>> {
        return currencyLayerClient.fetchCurrencyList()
    }

    private fun saveCurrencies(response: ClientResponse<List<Currency>>): Completable {
        return Completable.fromRunnable {
            when (response) {
                is ClientResponse.Success -> {
                    currencyDao.insertCurrencyList(response.data)
                }
            }
            currencyList.onNext(response)
        }
    }

    fun fetchCurrencyRates(source: String): Observable<ClientResponse<CurrencyRate>> {
        return if (!isNewSource(source)) {
            currencyRates
        } else {
            // Update rates with interval
            Observable.interval(RATE_UPDATE_TIME, TimeUnit.MILLISECONDS)
                .startWithItem(0)
                // Fetch currency rates from the database
                .flatMapSingle { fetchCurrencyRatesFromDatabase(source) }
                .subscribeOn(Schedulers.io())
                .flatMap {
                    if (it == CurrencyRate.Empty) {
                        // Fetch currency rates from the server and save to the database
                        fetchCurrencyRatesFromServer(source)
                            .flatMapCompletable { saveCurrencyRates(it) }
                            .andThen(currencyRates)
                    } else {
                        currencyRates.onNext(ClientResponse.Success(it))
                        currencyRates
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    private fun fetchCurrencyRatesFromDatabase(source: String): Single<CurrencyRate> {
        return Single.create { emitter ->
            var currencyRate = currencyRateDao.getCurrencyRate(source) ?: CurrencyRate.Empty
            // Check rate's timestump. If rates were updated more than RATE_UPDATE_TIME ago return CurrencyRate.Empty
            // CurrencyRate.Empty invokes update rates from the server side
            if (currencyRate.timestamp <= System.currentTimeMillis() - RATE_UPDATE_TIME) {
                currencyRate = CurrencyRate.Empty
            }
            if (!emitter.isDisposed) {
                emitter.onSuccess(currencyRate)
            }
        }
    }

    private fun fetchCurrencyRatesFromServer(source: String): Single<ClientResponse<CurrencyRate>> {
        // Free API access supports only USD
        return if (source != "USD") {
            fetchFakeCurrencyRates(source)
        } else {
            currencyLayerClient.fetchCurrencyRates(source)
        }
    }

    private fun fetchFakeCurrencyRates(source: String): Single<ClientResponse<CurrencyRate>> {
        return fetchCurrencyList()
            .flatMapSingle {
                when (it) {
                    is ClientResponse.Success<List<Currency>> -> {
                        fakeCurrencyRateGenerator.calculate(source, it.data)
                            .map { ClientResponse.Success(it) }
                    }
                    is ClientResponse.Error<List<Currency>> -> {
                        Single.just(ClientResponse.Error(it.error))
                    }
                }
            }
            .firstOrError()
    }

    private fun saveCurrencyRates(response: ClientResponse<CurrencyRate>): Completable {
        return Completable.fromRunnable {
            when (response) {
                is ClientResponse.Success -> {
                    currencyRateDao.insertCurrencyRate(response.data)
                    currencyRates.onNext(response)
                }
            }
        }
    }

    private fun isNewSource(source: String): Boolean {
        return if (currencyRates.hasValue()) {
            return when (val value = currencyRates.value) {
                is ClientResponse.Success -> value.data.source != source
                else -> true
            }
        } else true
    }
}