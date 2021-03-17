package com.valerii.coinapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse.Error
import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse.Success
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import com.valerii.coinapp.model.Quote
import com.valerii.coinapp.model.usecase.BaseCalculateRatesUseCase
import com.valerii.coinapp.model.usecase.CalculateRatesUseCase
import com.valerii.coinapp.network.MockCurrencyClient
import com.valerii.coinapp.persistence.MockCurrencyDao
import com.valerii.coinapp.persistence.MockCurrencyRateDao
import com.valerii.coinapp.repository.BaseFakeCurrencyRateGenerator
import com.valerii.coinapp.repository.MainRepository
import com.valerii.coinapp.utils.BigDecimalValueConverter
import com.valerii.coinapp.utils.BigDecimalValueFormatter
import com.valerii.coinapp.utils.ValueConverter
import com.valerii.coinapp.utils.ValueFormatter
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class MainViewModelTest {
    private val valueFormatter: ValueFormatter<BigDecimal> = BigDecimalValueFormatter()
    private val valueConverter: ValueConverter<BigDecimal> = BigDecimalValueConverter()
    private val calculateRatesUseCase: CalculateRatesUseCase = BaseCalculateRatesUseCase(
        valueConverter,
        valueFormatter
    )

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ ->
            Schedulers.from { it.run() }
        }
    }

    @Test
    fun `test - state, success response from repository`() {
        val currencyListResponse = Success(listOf(Currency("USD", "")))
        val currencyRatesResponse = Success(CurrencyRate("USD", 10, listOf(Quote("USDEUR", "10"))))
        val repository = MainRepository(
            MockCurrencyClient(
                currencyList = Single.just(currencyListResponse),
                currencyRates = Single.just(currencyRatesResponse)
            ),
            BaseFakeCurrencyRateGenerator(),
            MockCurrencyDao {},
            MockCurrencyRateDao {})

        val viewModel = MainViewModel(repository,
            calculateRatesUseCase,
            valueFormatter,
            valueConverter,
            SavedStateHandle()
        )


        // Initial state should be loading
        assertTrue(viewModel.isLoading.value!!)

        // OnStart
        viewModel.onStart()
        val latch = CountDownLatch(3)

        // Prepare currencies observer
        val currenciesObserver = object : Observer<List<String>> {
            override fun onChanged(o: List<String>) {
                latch.countDown()
                viewModel.currencies.removeObserver(this)
            }
        }
        viewModel.currencies.observeForever(currenciesObserver)

        // Prepare quotes observer
        val quotesObserver = object : Observer<List<Quote>> {
            override fun onChanged(o: List<Quote>) {
                latch.countDown()
                viewModel.quotes.removeObserver(this)
            }
        }
        viewModel.quotes.observeForever(quotesObserver)

        // Prepare loading observer
        val loadingObserver = object : Observer<Boolean> {
            override fun onChanged(o: Boolean) {
                latch.countDown()
                viewModel.isLoading.removeObserver(this)
            }
        }
        viewModel.isLoading.observeForever(loadingObserver)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw TimeoutException("Invalid state")
        }

        // Test view model state
        assertEquals(listOf("USD"), viewModel.currencies.value)
        assertEquals(listOf(Quote("USDEUR", "10.00")), viewModel.quotes.value)
        assertEquals(false, viewModel.isLoading.value)

        // OnStop
        viewModel.onStop()
    }

    @Test
    fun `test - state, error response from repository`() {
        val currencyListResponse = Error<List<Currency>>("Server error code: 101")
        val currencyRatesResponse = Error<CurrencyRate>("Server error code: 102")
        val repository = MainRepository(
            MockCurrencyClient(
                currencyList = Single.just(currencyListResponse),
                currencyRates = Single.just(currencyRatesResponse)
            ),
            BaseFakeCurrencyRateGenerator(),
            MockCurrencyDao {},
            MockCurrencyRateDao {})

        val viewModel = MainViewModel(repository,
            calculateRatesUseCase,
            valueFormatter,
            valueConverter,
            SavedStateHandle()
        )


        // Initial state should be loading
        assertTrue(viewModel.isLoading.value!!)

        // OnStart
        viewModel.onStart()
        val latch = CountDownLatch(1)

        // Prepare error observer
        val errorObserver = object : Observer<String> {
            override fun onChanged(o: String) {
                latch.countDown()
                viewModel.error.removeObserver(this)
            }
        }
        viewModel.error.observeForever(errorObserver)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw TimeoutException("Invalid state")
        }

        // Test view model state
        assertEquals(null, viewModel.currencies.value)
        assertEquals(null, viewModel.quotes.value)
        assertEquals(true, viewModel.isLoading.value)
        assertEquals("Server error code: 101", viewModel.error.value)

        // OnStop
        viewModel.onStop()
    }

    @Test
    fun `test - select currency, success response from repository`() {
        val currencyListResponse = Success(listOf(Currency("USD", ""), Currency("EUR", "")))
        val currencyRatesResponse = Success(CurrencyRate("USD", 10, listOf(Quote("USDEUR", "10"))))
        val repository = MainRepository(
            MockCurrencyClient(
                currencyList = Single.just(currencyListResponse),
                currencyRates = Single.just(currencyRatesResponse)
            ),
            BaseFakeCurrencyRateGenerator(),
            MockCurrencyDao {},
            MockCurrencyRateDao {})

        val viewModel = MainViewModel(repository,
            calculateRatesUseCase,
            valueFormatter,
            valueConverter,
            SavedStateHandle()
        )


        // Initial state should be loading
        assertTrue(viewModel.isLoading.value!!)

        // OnStart
        viewModel.onStart()
        val latch = CountDownLatch(1)


        // Prepare quotes observer
        val quotesObserver = object : Observer<List<Quote>> {
            var firstUpdate = true

            override fun onChanged(o: List<Quote>) {
                latch.countDown()
                viewModel.quotes.removeObserver(this)
                firstUpdate = false
            }
        }
        viewModel.quotes.observeForever(quotesObserver)

        // Select currency
        viewModel.selectCurrency("EUR")

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw TimeoutException("Invalid state")
        }

        // Test view model state
        assertEquals(listOf("USD", "EUR"), viewModel.currencies.value)
        assertEquals("EUR", viewModel.selected.value)
        viewModel.quotes.value!!.forEach {
            assertTrue(it.quote.contains("EUR"))
        }
        assertEquals(false, viewModel.isLoading.value)

        // OnStop
        viewModel.onStop()
    }

    @Test
    fun `test - update currency value, success response from repository`() {
        val currencyListResponse = Success(listOf(Currency("USD", ""), Currency("EUR", "")))
        val currencyRatesResponse = Success(CurrencyRate("USD", 10, listOf(Quote("USDEUR", "10"))))
        val repository = MainRepository(
            MockCurrencyClient(
                currencyList = Single.just(currencyListResponse),
                currencyRates = Single.just(currencyRatesResponse)
            ),
            BaseFakeCurrencyRateGenerator(),
            MockCurrencyDao {},
            MockCurrencyRateDao {})

        val viewModel = MainViewModel(repository,
            calculateRatesUseCase,
            valueFormatter,
            valueConverter,
            SavedStateHandle()
        )


        // Initial state should be loading
        assertTrue(viewModel.isLoading.value!!)

        // OnStart
        viewModel.onStart()
        val latch = CountDownLatch(1)


        // Prepare quotes observer
        val quotesObserver = object : Observer<List<Quote>> {
            var firstUpdate = true

            override fun onChanged(o: List<Quote>) {
                latch.countDown()
                viewModel.quotes.removeObserver(this)
                firstUpdate = false
            }
        }
        viewModel.quotes.observeForever(quotesObserver)

        // Select currency
        viewModel.updateCurrencyValue("10")

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw TimeoutException("Invalid state")
        }

        // Test view model state
        assertEquals(listOf("USD", "EUR"), viewModel.currencies.value)
        assertEquals("USD", viewModel.selected.value)
        assertEquals(listOf(Quote("USDEUR", "100.00")), viewModel.quotes.value) // Calculated
        assertEquals(false, viewModel.isLoading.value)

        // OnStop
        viewModel.onStop()
    }
}