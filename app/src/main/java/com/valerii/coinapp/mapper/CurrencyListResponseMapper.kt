package com.valerii.coinapp.mapper

import com.valerii.coinapp.mapper.ResponseMapper.ClientResponse
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.network.response.CurrencyListResponse
import retrofit2.Response

object CurrencyListResponseMapper : ResponseMapper<CurrencyListResponse, List<Currency>> {
    override fun map(response: Response<CurrencyListResponse>): ClientResponse<List<Currency>> {
        val currencyListResponse = response.body()
        return if (response.isSuccessful && currencyListResponse?.success == true) {
            ClientResponse.Success(currencyListResponse.currencies)
        } else {
            ClientResponse.Error(response.message())
        }
    }
}