package com.valerii.coinapp.model.usecase

import com.valerii.coinapp.model.Quote
import com.valerii.coinapp.utils.BigDecimalValueConverter
import com.valerii.coinapp.utils.ValueConverter
import com.valerii.coinapp.utils.ValueFormatter
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class BaseCalculateRatesUseCaseTest {
    private val noFormat = object : ValueFormatter<BigDecimal> {
        override fun format(value: BigDecimal): String {
            return value.toPlainString()
        }
    }

    @Test
    fun `test - calculate, empty values`() {
        val quotes = ArrayList<Quote>().apply {
            add(Quote("One", ""))
            add(Quote("Two", "1"))
            add(Quote("Three", ""))
        }

        val valueConverter = BigDecimalValueConverter()
        val calculateRatesUseCase = BaseCalculateRatesUseCase(
            valueConverter,
            noFormat
        )
        val sourceValue = BigDecimal.ONE
        val test = calculateRatesUseCase.calculate(sourceValue, quotes).test()
        test.await(5, TimeUnit.SECONDS)
        test.assertValues(
            listOf(
                Quote("One", ValueConverter.NaN),
                Quote("Two", "1"),
                Quote("Three", ValueConverter.NaN)
            )
        )
        test.assertComplete()
    }

    @Test
    fun `test - calculate, unsupported values`() {
        val quotes = ArrayList<Quote>().apply {
            add(Quote("One", "2asd"))
            add(Quote("Two", "1"))
            add(Quote("Three", "909.as"))
        }

        val valueConverter = BigDecimalValueConverter()
        val calculateRatesUseCase = BaseCalculateRatesUseCase(
            valueConverter,
            noFormat
        )
        val sourceValue = BigDecimal.ONE
        val test = calculateRatesUseCase.calculate(sourceValue, quotes).test()
        test.await(5, TimeUnit.SECONDS)
        test.assertValues(
            listOf(
                Quote("One", ValueConverter.NaN),
                Quote("Two", "1"),
                Quote("Three", ValueConverter.NaN)
            )
        )
        test.assertComplete()
    }

    @Test
    fun `test - calculate, correct values`() {
        val quotes = ArrayList<Quote>().apply {
            add(Quote("One", "1"))
            add(Quote("Two", "10"))
            add(Quote("Three", "1.2"))
            add(Quote("Three", "2.2"))
        }

        val valueConverter = BigDecimalValueConverter()
        val calculateRatesUseCase = BaseCalculateRatesUseCase(
            valueConverter,
            noFormat
        )
        val sourceValue = BigDecimal.valueOf(10)
        val test = calculateRatesUseCase.calculate(sourceValue, quotes).test()
        test.await(5, TimeUnit.SECONDS)
        test.assertValues(
            listOf(
                Quote("One", "10"),
                Quote("Two", "100"),
                Quote("Three", "12.0"),
                Quote("Three", "22.0")
            )
        )
        test.assertComplete()
    }
}