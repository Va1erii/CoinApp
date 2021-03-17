package com.valerii.coinapp.utils

import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class BigDecimalValueFormatterTest {
    @Test
    fun testFormat() {
        val formatter = BigDecimalValueFormatter()
        Assert.assertEquals("1.00", formatter.format(BigDecimal.valueOf(1)))
        Assert.assertEquals("1.10", formatter.format(BigDecimal.valueOf(1.1)))
        Assert.assertEquals("1,000.00", formatter.format(BigDecimal.valueOf(1_000)))
        Assert.assertEquals("1,000,000.90", formatter.format(BigDecimal.valueOf(1_000_000.9)))
    }
}