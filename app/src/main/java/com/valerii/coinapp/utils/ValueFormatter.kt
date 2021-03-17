package com.valerii.coinapp.utils

import java.math.BigDecimal
import java.text.DecimalFormat

interface ValueFormatter<Y> {
    fun format(value: Y): String
}

class BigDecimalValueFormatter : ValueFormatter<BigDecimal> {
    private val formatter: DecimalFormat = DecimalFormat("###,##0.00")

    override fun format(value: BigDecimal): String {
        return formatter.format(value)
    }
}