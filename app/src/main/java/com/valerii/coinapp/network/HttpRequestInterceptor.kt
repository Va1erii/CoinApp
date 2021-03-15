package com.valerii.coinapp.network

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class HttpRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.newBuilder()
            .addQueryParameter("access_key", ApiKey.apiKey())
            .build()
        val request = originalRequest.newBuilder()
            .url(requestUrl)
            .build()
        Timber.d(request.toString())
        return chain.proceed(request)
    }
}