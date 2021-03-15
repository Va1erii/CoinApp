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
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val currencyLayerClient: CurrencyLayerClient,
    private val currencyDao: CurrencyDao,
    private val currencyRateDao: CurrencyRateDao
) {
    private val currencyList: BehaviorSubject<ClientResponse<List<Currency>>> =
        BehaviorSubject.create()
    private val currencyRates: BehaviorSubject<ClientResponse<CurrencyRate>> =
        BehaviorSubject.create()

    fun fetchCurrencyList(): Observable<ClientResponse<List<Currency>>> {
        return if (currencyList.hasValue()) {
            currencyList
        } else {
            Observable
                .create<List<Currency>> { it.onNext(currencyDao.getCurrencyList()) }
                .subscribeOn(Schedulers.io())
                .flatMap {
                    if (it.isEmpty()) {
                        currencyLayerClient.fetchCurrencyList()
                            .flatMapCompletable {
                                Completable.fromRunnable {
                                    if (it is ClientResponse.Success) {
                                        currencyDao.insertCurrencyList(it.data)
                                    }
                                    currencyList.onNext(it)
                                }
                            }
                            .andThen(currencyList)
                    } else {
                        currencyList.onNext(ClientResponse.Success(it))
                        currencyList
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun fetchCurrencyRates(source: String): Observable<ClientResponse<CurrencyRate>> {
        return if (currencyRates.hasValue()) {
            currencyRates
        } else {
            Observable
                .create<CurrencyRate> {
                    it.onNext(
                        currencyRateDao.getCurrencyRate(source) ?: CurrencyRate.Empty
                    )
                }
                .subscribeOn(Schedulers.io())
                .flatMap {
                    if (it == CurrencyRate.Empty) {
                        currencyLayerClient.fetchCurrencyRates(source)
                            .flatMapCompletable {
                                Completable.fromRunnable {
                                    if (it is ClientResponse.Success) {
                                        currencyRateDao.insertCurrencyRate(it.data)
                                    }
                                    currencyRates.onNext(it)
                                }
                            }
                            .andThen(currencyRates)
                    } else {
                        currencyRates.onNext(ClientResponse.Success(it))
                        currencyRates
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}