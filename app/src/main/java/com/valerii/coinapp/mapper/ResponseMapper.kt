package com.valerii.coinapp.mapper

import retrofit2.Response

interface ResponseMapper<In, Out> {
    fun map(response: Response<In>): ClientResponse<Out>

    sealed class ClientResponse<T> {
        data class Success<T>(val data: T) : ClientResponse<T>()
        data class Error<T>(val error: String) : ClientResponse<T>()
    }
}