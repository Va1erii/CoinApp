package com.valerii.coinapp.ui.main

import androidx.lifecycle.*
import com.valerii.coinapp.extension.addTo
import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse
import com.valerii.coinapp.model.Quote
import com.valerii.coinapp.model.usecase.CalculateRatesUseCase
import com.valerii.coinapp.repository.MainRepository
import com.valerii.coinapp.utils.BigDecimalValueConverter
import com.valerii.coinapp.utils.BigDecimalValueConverter.ConvertResult.*
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
    private val valueConverter: BigDecimalValueConverter,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), LifecycleObserver {
    companion object {
        const val SELECTED_CURRENCY_KEY = "selected.currency.key"
        val DEFAULT_VALUE_IF_EMPTY: BigDecimal = BigDecimal.ONE
    }

    private val selectedCurrency = BehaviorSubject.create<String>()
    private val selectedCurrencyValue = BehaviorSubject.createDefault(DEFAULT_VALUE_IF_EMPTY)
    private val baseQuotes = ArrayList<Quote>()
    private val compositeDisposable = CompositeDisposable()

    val selected: MutableLiveData<String> = MutableLiveData()
    val defaultValue: MutableLiveData<String> = MutableLiveData(valueConverter.convert(
        DEFAULT_VALUE_IF_EMPTY)
    )
    val quotes: MutableLiveData<List<Quote>> = MutableLiveData()
    val currencies: MutableLiveData<List<String>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val error: MutableLiveData<String> = MutableLiveData()

    fun selectCurrency(name: String) {
        selected.value = name
        selectedCurrency.onNext(name)
        savedStateHandle[SELECTED_CURRENCY_KEY] = name
    }

    fun updateCurrencyValue(value: String) {
        when (val convertResult = valueConverter.convert(value)) {
            Empty -> selectedCurrencyValue.onNext(DEFAULT_VALUE_IF_EMPTY)
            is Success -> selectedCurrencyValue.onNext(convertResult.value)
            is Error -> error.value = "Value validation error"
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        fetchCurrencies()
        handleCurrencyChange()
        handleSelectedCurrencyValueChange()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        compositeDisposable.clear()
    }

    private fun fetchCurrencies() {
        // Fetch currency list
        mainRepository.fetchCurrencyList()
            .subscribe {
                when (it) {
                    is ClientResponse.Success -> {
                        // Data should not be empty
                        if (it.data.isEmpty()) {
                            error.value = "Empty response"
                        } else {
                            val availableCurrencies = it.data.asSequence().map { it.name }.toList()
                            currencies.value = availableCurrencies
                            processSelectedCurrency(availableCurrencies)
                        }
                    }
                    is ClientResponse.Error -> error.value = it.error
                }
            }.addTo(compositeDisposable)
    }

    private fun processSelectedCurrency(availableCurrencies: List<String>) {
        // Check saved currency
        val savedCurrency: String? =
            savedStateHandle.get(SELECTED_CURRENCY_KEY)
        savedCurrency
            ?.let { availableCurrencies.contains(it) }
            ?.run {
                // Select saved currency
                selected.value = savedCurrency
                selectedCurrency.onNext(savedCurrency)
            }
            ?: run {
                // Currency was not selected yet. Select first available currency and save to state
                selected.value = availableCurrencies.first()
                selectedCurrency.onNext(selected.value)
                savedStateHandle[SELECTED_CURRENCY_KEY] = selected.value
            }
    }

    private fun handleCurrencyChange() {
        selectedCurrency
            // Only if it was changed
            .distinctUntilChanged()
            .doOnNext { isLoading.value = true }
            .switchMap { mainRepository.fetchCurrencyRates(it) }
            .switchMap<ClientResponse<List<Quote>>> {
                when (it) {
                    // Calculate currencies with source base value and prepare quotes
                    is ClientResponse.Success -> {
                        baseQuotes.clear()
                        baseQuotes.addAll(it.data.quotes)
                        calculateRatesUseCase.calculate(selectedCurrencyValue.value, baseQuotes)
                            .map { ClientResponse.Success(it) }
                    }
                    is ClientResponse.Error -> Observable.just(ClientResponse.Error(it.error))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is ClientResponse.Success -> {
                        isLoading.value = false
                        quotes.value = it.data
                    }
                    is ClientResponse.Error -> error.value = it.error
                }
            }.addTo(compositeDisposable)
    }

    private fun handleSelectedCurrencyValueChange() {
        selectedCurrencyValue
            .distinctUntilChanged()
            // Make sure that data was loaded
            .filter { baseQuotes.isNotEmpty() }
            .switchMap { calculateRatesUseCase.calculate(it, baseQuotes) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                quotes.value = it
            }.addTo(compositeDisposable)
    }
}