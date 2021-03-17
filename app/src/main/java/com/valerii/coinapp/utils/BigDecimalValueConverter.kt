package com.valerii.coinapp.utils

import java.math.BigDecimal
import java.text.DecimalFormat

class BigDecimalValueConverter {
    private val formatter: DecimalFormat = DecimalFormat("###,##0.00")

    fun convert(value: BigDecimal): String {
        return formatter.format(value)
    }

    fun convert(value: String): ConvertResult {
        return if (value.isEmpty()) {
            ConvertResult.Empty
        } else {
            try {
                val convertedValue = value.toBigDecimal()
                ConvertResult.Success(convertedValue)
            } catch (e: NumberFormatException) {
                ConvertResult.Error
            }
        }
    }

    sealed class ConvertResult {
        object Empty : ConvertResult()
        object Error : ConvertResult()
        data class Success(val value: BigDecimal) : ConvertResult()
    }
}