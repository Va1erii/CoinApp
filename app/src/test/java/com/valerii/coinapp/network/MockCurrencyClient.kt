package com.valerii.coinapp.network

import com.valerii.coinapp.mapper.ResponseMapper
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.CurrencyRate
import io.reactivex.rxjava3.core.Single

class MockCurrencyClient(
    private val currencyList: Single<ResponseMapper.ClientResponse<List<Currency>>>,
    private val currencyRates: Single<ResponseMapper.ClientResponse<CurrencyRate>>,
) : CurrencyLayerClient {

    override fun fetchCurrencyList(): Single<ResponseMapper.ClientResponse<List<Currency>>> {
        return currencyList
    }

    override fun fetchCurrencyRates(source: String): Single<ResponseMapper.ClientResponse<CurrencyRate>> {
        return currencyRates
    }
}