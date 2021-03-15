package com.valerii.coinapp.ui.main

import androidx.lifecycle.*
import com.valerii.coinapp.extension.addTo
import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.Quote
import com.valerii.coinapp.model.usecase.CalculateRatesUseCase
import com.valerii.coinapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val calculateRatesUseCase: CalculateRatesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), LifecycleObserver {
    private val compositeDisposable = CompositeDisposable()
    private val selectedCurrency = BehaviorSubject.create<Currency>()
    private val baseCurrencyValue = BehaviorSubject.createDefault(BigDecimal.ONE)
    private val availableCurrencies = ArrayList<Currency>()
    private val baseQuotes = ArrayList<Quote>()

    val quotes: MutableLiveData<List<Quote>> = MutableLiveData()
    val currencies: MutableLiveData<List<String>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val error: MutableLiveData<String> = MutableLiveData()

    fun selectCurrency(name: String) {
        availableCurrencies.find { it.name == name }?.let {
            selectedCurrency.onNext(it)
        }
    }

    fun updateCurrencyValue(value: String) {
        baseCurrencyValue.onNext(value.toBigDecimal())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        mainRepository.fetchCurrencyList()
            .subscribe {
                when (it) {
                    is ClientResponse.Success -> {
                        availableCurrencies.clear()
                        availableCurrencies.addAll(it.data)
                        currencies.value = availableCurrencies.map { it.name }.toList()
                        if (!selectedCurrency.hasValue()) {
                            selectedCurrency.onNext(it.data.first())
                        }
                    }
                    is ClientResponse.Error -> error.value = it.error
                }
            }.addTo(compositeDisposable)

        selectedCurrency
            .distinctUntilChanged()
            .doOnNext { isLoading.value = true }
            .switchMap { mainRepository.fetchCurrencyRates(it.name) }
            .switchMap {
                when (it) {
                    is ClientResponse.Success -> calculateRatesUseCase.calculate(
                        baseCurrencyValue.value,
                        it.data.quotes
                    )
                    is ClientResponse.Error -> Observable.just(it)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is List<*> -> {
                        isLoading.value = false
                        baseQuotes.clear()
                        baseQuotes.addAll(it as List<Quote>)
                        quotes.value = baseQuotes
                    }
                    is ClientResponse.Error<*> -> error.value = it.error
                }
            }.addTo(compositeDisposable)

        baseCurrencyValue
            .distinctUntilChanged()
            .filter { baseQuotes.isNotEmpty()}
            .switchMap { calculateRatesUseCase.calculate(it, baseQuotes) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                quotes.value = it
            }.addTo(compositeDisposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        compositeDisposable.clear()
    }
}