package com.valerii.coinapp.utils

import com.valerii.coinapp.utils.ValueConverter.ConvertResult
import java.math.BigDecimal

interface ValueConverter<Y> {
    fun convert(value: String): ConvertResult<Y>
    fun convertToString(value: Y, formatter: ValueFormatter<Y>): String

    sealed class ConvertResult<Y> {
        class Empty<Y> : ConvertResult<Y>()
        class Error<Y> : ConvertResult<Y>()
        data class Success<Y>(val value: Y) : ConvertResult<Y>()
    }

    companion object {
        // Used for unsupported values
        const val NaN = "NaN"
    }
}

class BigDecimalValueConverter : ValueConverter<BigDecimal> {
    override fun convert(value: String): ConvertResult<BigDecimal> {
        return if (value.isEmpty()) {
            ConvertResult.Empty()
        } else {
            try {
                val convertedValue = value.toBigDecimal()
                ConvertResult.Success(convertedValue)
            } catch (e: NumberFormatException) {
                ConvertResult.Error()
            }
        }
    }

    override fun convertToString(value: BigDecimal, formatter: ValueFormatter<BigDecimal>): String {
        return formatter.format(value)
    }
}