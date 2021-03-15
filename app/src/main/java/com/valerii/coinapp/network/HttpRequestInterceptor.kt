package com.valerii.coinapp.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class HttpRequestInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.newBuilder()
            .addQueryParameter("access_key", apiKey)
            .build()
        val request = originalRequest.newBuilder()
            .url(requestUrl)
            .build()
        Log.d("TAG", request.toString())
        return chain.proceed(request)
    }
}