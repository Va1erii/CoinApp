package com.valerii.coinapp.repository

import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse.Error
import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse.Success
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import com.valerii.coinapp.network.MockCurrencyClient
import com.valerii.coinapp.persistence.MockCurrencyDao
import com.valerii.coinapp.persistence.MockCurrencyRateDao
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

class MainRepositoryTest {

    @Before
    fun init() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ ->
            Schedulers.from { it.run() }
        }
    }

    @Test
    fun `test - fetchCurrencyList, success load from database`() {
        val currencyLayerClient = MockCurrencyClient(
            currencyList = Single.just(Success(emptyList())),
            currencyRates = Single.just(Success(CurrencyRate.Empty))
        )
        val fakeCurrencyRateGenerator = MockFakeCurrencyRateGenerator()

        val currencyListSaved = AtomicBoolean(false)
        val currencyRateSaved = AtomicBoolean(false)
        val currencyRateDao = MockCurrencyRateDao {
            currencyRateSaved.set(true)
        }
        val currencyDao = MockCurrencyDao {
            currencyListSaved.set(true)
        }
        // Prepare mocked result for database
        val databaseResult = listOf(
            Currency("one", ""),
            Currency("two", ""),
            Currency("three", "")
        )
        currencyDao.result = databaseResult

        val repository = MainRepository(
            currencyLayerClient = currencyLayerClient,
            fakeCurrencyRateGenerator = fakeCurrencyRateGenerator,
            currencyDao = currencyDao,
            currencyRateDao = currencyRateDao
        )
        val test = repository.fetchCurrencyList().test()

        // Should return database result
        test.awaitCount(1)
        test.assertValues(Success(databaseResult))
        test.dispose()

        // Success fetch from database, so it should not be called
        assertFalse(currencyListSaved.get())
    }

    @Test
    fun `test - fetchCurrencyList, empty response from database, success from the server`() {
        // Prepare server response
        val serverResponse = Success(
            listOf(
                Currency("one", ""),
                Currency("two", ""),
                Currency("three", "")
            )
        )

        val currencyLayerClient = MockCurrencyClient(
            currencyList = Single.just(serverResponse),
            currencyRates = Single.just(Success(CurrencyRate.Empty))
        )
        val fakeCurrencyRateGenerator = MockFakeCurrencyRateGenerator()

        val currencyListSaved = AtomicBoolean(false)
        val currencyRateSaved = AtomicBoolean(false)
        val currencyRateDao = MockCurrencyRateDao {
            currencyRateSaved.set(true)
        }
        val currencyDao = MockCurrencyDao {
            currencyListSaved.set(true)
        }
        // Prepare mocked result for database. Empty
        val databaseResult = emptyList<Currency>()
        currencyDao.result = databaseResult

        val repository = MainRepository(
            currencyLayerClient = currencyLayerClient,
            fakeCurrencyRateGenerator = fakeCurrencyRateGenerator,
            currencyDao = currencyDao,
            currencyRateDao = currencyRateDao
        )
        val test = repository.fetchCurrencyList().test()

        // Should return server result
        test.awaitCount(1)
        test.assertValues(serverResponse)
        test.dispose()

        // Success response from the server, so it should be saved to database
        assertTrue(currencyListSaved.get())
    }

    @Test
    fun `test - fetchCurrencyList, empty response from database, error from the server`() {
        // Prepare server response
        val serverResponse = Error<List<Currency>>("Server error")

        val currencyLayerClient = MockCurrencyClient(
            currencyList = Single.just(serverResponse),
            currencyRates = Single.just(Success(CurrencyRate.Empty))
        )
        val fakeCurrencyRateGenerator = MockFakeCurrencyRateGenerator()

        val currencyListSaved = AtomicBoolean(false)
        val currencyRateSaved = AtomicBoolean(false)
        val currencyRateDao = MockCurrencyRateDao {
            currencyRateSaved.set(true)
        }
        val currencyDao = MockCurrencyDao {
            currencyListSaved.set(true)
        }
        // Prepare mocked result for database. Empty
        val databaseResult = emptyList<Currency>()
        currencyDao.result = databaseResult

        val repository = MainRepository(
            currencyLayerClient = currencyLayerClient,
            fakeCurrencyRateGenerator = fakeCurrencyRateGenerator,
            currencyDao = currencyDao,
            currencyRateDao = currencyRateDao
        )
        val test = repository.fetchCurrencyList().test()

        // Should return error from the server
        test.awaitCount(1)
        test.assertValues(serverResponse)
        test.dispose()

        // Error response from the server, so it should not be called
        assertFalse(currencyListSaved.get())
    }
}