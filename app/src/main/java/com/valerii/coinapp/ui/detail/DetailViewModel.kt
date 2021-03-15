package com.valerii.coinapp.ui.detail

import androidx.lifecycle.*
import com.valerii.coinapp.extension.addTo
import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.usecase.CalculateRatesUseCase
import com.valerii.coinapp.repository.DetailRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

class DetailViewModel @AssistedInject constructor(
    private val detailRepository: DetailRepository,
    private val calculateRatesUseCase: CalculateRatesUseCase,
    @Assisted private val source: String
) : ViewModel(), LifecycleObserver {
    private val compositeDisposable = CompositeDisposable()
    private val baseSourceValue: BehaviorSubject<BigDecimal> = BehaviorSubject.createDefault(
        BigDecimal.ONE
    )
    val baseCurrency: MutableLiveData<Currency> = MutableLiveData()
    val currencies: MutableLiveData<List<Currency>> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)

    fun updateBaseSourceValue(value: String) {
        baseSourceValue.onNext(value.toBigDecimal())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
//        detailRepository.fetchCurrencyList()
//            .map {
//                when(it) {
//                    is ClientResponse.Success -> it.data
//                    is ClientResponse.Error -> null
//                }
//            }
//            .flatMapCompletable {
//                Completable.fromRunnable {
//                    currencies.value = it!!
//                    baseCurrency.value = it.find { it.name == "USD" }
//                }
//            }
//            .andThen(detailRepository.fetchCurrencyRates(baseCurrency.value!!.name))
//            .map {  }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        compositeDisposable.clear()
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(source: String): DetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            source: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(source) as T
            }
        }
    }
}