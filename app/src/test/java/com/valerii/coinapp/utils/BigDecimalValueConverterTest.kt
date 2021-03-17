package com.valerii.coinapp.utils

import com.valerii.coinapp.utils.ValueConverter.ConvertResult.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class BigDecimalValueConverterTest {
    private val noFormat = object : ValueFormatter<BigDecimal> {
        override fun format(value: BigDecimal): String {
            return value.toPlainString()
        }
    }

    @Test
    fun testConvert() {
        val converter = BigDecimalValueConverter()

        assertEquals(Success<BigDecimal>(BigDecimal.valueOf(1)), converter.convert("1"))
        assertEquals(Success<BigDecimal>(BigDecimal.valueOf(1.1)), converter.convert("1.1"))
        assertTrue(converter.convert("NaN") is Error)
        assertTrue(converter.convert("q23") is Error)
        assertTrue(converter.convert("") is Empty)
    }

    @Test
    fun testConvertToString() {
        val converter = BigDecimalValueConverter()

        assertEquals("1", converter.convertToString(BigDecimal.valueOf(1), noFormat))
        assertEquals("1.1", converter.convertToString(BigDecimal.valueOf(1.1), noFormat))
    }
}