package com.valerii.coinapp.repository

import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse.Error
import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse.Success
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import com.valerii.coinapp.model.Quote
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
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MainRepositoryTest {

    @Before
    fun setup() {
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

    @Test
    fun `test - fetchCurrencyRates, success load from database, within rate update time`() {
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
        val databaseResult =
            CurrencyRate("source", System.currentTimeMillis(), listOf(Quote("one", "1")))
        currencyRateDao.result = databaseResult

        val repository = MainRepository(
            currencyLayerClient = currencyLayerClient,
            fakeCurrencyRateGenerator = fakeCurrencyRateGenerator,
            currencyDao = currencyDao,
            currencyRateDao = currencyRateDao
        )
        val test = repository.fetchCurrencyRates("source").test()

        // Should return database result
        test.awaitCount(1)
        test.assertValues(Success(databaseResult))
        test.dispose()

        // Success fetch from database, so it should not be called
        assertFalse(currencyRateSaved.get())
    }

    @Test
    fun `test - fetchCurrencyRates, success load from database, but load from the server because of rate update time`() {
        val serverResponse =
            Success(CurrencyRate("USD", System.currentTimeMillis(), listOf(Quote("one", "1"))))
        val currencyLayerClient = MockCurrencyClient(
            currencyList = Single.just(Success(emptyList())),
            currencyRates = Single.just(serverResponse)
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
        val timestump = System.currentTimeMillis() - MainRepository.RATE_UPDATE_TIME - 1000
        val databaseResult =
            CurrencyRate("USD", timestump, listOf(Quote("one", "1")))
        currencyRateDao.result = databaseResult

        val repository = MainRepository(
            currencyLayerClient = currencyLayerClient,
            fakeCurrencyRateGenerator = fakeCurrencyRateGenerator,
            currencyDao = currencyDao,
            currencyRateDao = currencyRateDao
        )
        val test = repository.fetchCurrencyRates("USD").test()

        // Should return server result, because rate needs update
        test.awaitCount(1)
        test.assertValues(serverResponse)
        test.dispose()

        // Success fetch from database, but they were updated from the server
        assertTrue(currencyRateSaved.get())
    }

    @Test
    fun `test - fetchCurrencyRates, failed load from database, success load from the server`() {
        val serverResponse =
            Success(CurrencyRate("USD", System.currentTimeMillis(), listOf(Quote("one", "1"))))
        val currencyLayerClient = MockCurrencyClient(
            currencyList = Single.just(Success(emptyList())),
            currencyRates = Single.just(serverResponse)
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
        // Prepare database result
        currencyRateDao.result = null

        val repository = MainRepository(
            currencyLayerClient = currencyLayerClient,
            fakeCurrencyRateGenerator = fakeCurrencyRateGenerator,
            currencyDao = currencyDao,
            currencyRateDao = currencyRateDao
        )
        val test = repository.fetchCurrencyRates("USD").test()

        // Should return server result
        test.awaitCount(1)
        test.assertValues(serverResponse)
        test.dispose()

        // Success from the server, so the result should be saved
        assertTrue(currencyRateSaved.get())
    }

    @Test
    fun `test - fetchCurrencyRates, failed load from database, error from the server`() {
        val serverResponse = Error<CurrencyRate>("Server error")
        val currencyLayerClient = MockCurrencyClient(
            currencyList = Single.just(Success(emptyList())),
            currencyRates = Single.just(serverResponse)
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
        // Prepare database result
        currencyRateDao.result = CurrencyRate.Empty

        val repository = MainRepository(
            currencyLayerClient = currencyLayerClient,
            fakeCurrencyRateGenerator = fakeCurrencyRateGenerator,
            currencyDao = currencyDao,
            currencyRateDao = currencyRateDao
        )
        val test = repository.fetchCurrencyRates("USD").test()

        // Should return server result
        test.awaitCount(1)
        test.assertValues(serverResponse)
        test.dispose()

        // Error from the server, so the result should NOT be saved
        assertFalse(currencyRateSaved.get())
    }

    @Test
    fun `test - fetchCurrencyRates, rates update in update time`() {
        val serverResponse =
            Success(CurrencyRate("USD", System.currentTimeMillis(), listOf(Quote("one", "1"))))
        val currencyLayerClient = MockCurrencyClient(
            currencyList = Single.just(Success(emptyList())),
            currencyRates = Single.just(serverResponse)
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
        // Prepare database result
        currencyRateDao.result = CurrencyRate.Empty

        val repository = MainRepository(
            currencyLayerClient = currencyLayerClient,
            fakeCurrencyRateGenerator = fakeCurrencyRateGenerator,
            currencyDao = currencyDao,
            currencyRateDao = currencyRateDao,
            rateUpdateTime = 100
        )
        val test = repository.fetchCurrencyRates("USD").test()

        // Should updates server result
        test.await(2, TimeUnit.SECONDS)
        assertTrue(test.values().size > 1)
        test.dispose()

        // Success from the server, so the result should be saved
        assertTrue(currencyRateSaved.get())
    }
}