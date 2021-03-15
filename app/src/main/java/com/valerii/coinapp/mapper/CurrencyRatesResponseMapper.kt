package com.valerii.coinapp.mapper

import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse
import com.valerii.coinapp.model.CurrencyRate
import com.valerii.coinapp.network.response.CurrencyRatesResponse
import retrofit2.Response

object CurrencyRatesResponseMapper : ResponseMapper<CurrencyRatesResponse, CurrencyRate> {
    override fun map(response: Response<CurrencyRatesResponse>): ClientResponse<CurrencyRate> {
        val currencyRatesResponse = response.body()
        return if (response.isSuccessful && currencyRatesResponse?.success == true) {
            ClientResponse.Success(
                CurrencyRate(
                    currencyRatesResponse.source,
                    currencyRatesResponse.timestamp,
                    currencyRatesResponse.quotes
                )
            )
        } else {
            ClientResponse.Error(response.message())
        }
    }
}