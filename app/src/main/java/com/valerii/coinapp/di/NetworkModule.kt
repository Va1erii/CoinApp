package com.valerii.coinapp.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.valerii.coinapp.R
import com.valerii.coinapp.mapper.CurrencyListResponseMapper
import com.valerii.coinapp.mapper.CurrencyRatesResponseMapper
import com.valerii.coinapp.model.Currency
import com.valerii.coinapp.model.Quote
import com.valerii.coinapp.network.CurrencyLayerClient
import com.valerii.coinapp.network.CurrencyLayerService
import com.valerii.coinapp.network.HttpRequestInterceptor
import com.valerii.coinapp.network.adapter.CurrencyListAdapter
import com.valerii.coinapp.network.adapter.QuoteListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(
        currencyListAdapter: CurrencyListAdapter,
        quoteListAdapter: QuoteListAdapter,
    ): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(
                Types.newParameterizedType(List::class.java, Currency::class.java),
                currencyListAdapter
            )
            .add(
                Types.newParameterizedType(List::class.java, Quote::class.java),
                quoteListAdapter
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext applicationContext: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpRequestInterceptor(applicationContext.getString(R.string.api_key)))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://api.currencylayer.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyLayerService(retrofit: Retrofit): CurrencyLayerService {
        return retrofit.create(CurrencyLayerService::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyLayerClient(
        currencyLayerService: CurrencyLayerService,
        currencyListResponseMapper: CurrencyListResponseMapper,
        currencyRatesResponseMapper: CurrencyRatesResponseMapper,
    ): CurrencyLayerClient {
        return CurrencyLayerClient(
            currencyLayerService,
            currencyListResponseMapper,
            currencyRatesResponseMapper
        )
    }

    @Provides
    @Singleton
    fun provideCurrencyListAdapter(): CurrencyListAdapter {
        return CurrencyListAdapter()
    }

    @Provides
    @Singleton
    fun provideQuoteListAdapter(): QuoteListAdapter {
        return QuoteListAdapter()
    }


    @Provides
    @Singleton
    fun provideCurrencyListResponseMapper(): CurrencyListResponseMapper {
        return CurrencyListResponseMapper
    }

    @Provides
    @Singleton
    fun provideCurrencyRatesResponseMapper(): CurrencyRatesResponseMapper {
        return CurrencyRatesResponseMapper
    }
}