package com.valerii.coinapp.di

import com.valerii.coinapp.network.CurrencyLayerClient
import com.valerii.coinapp.persistence.CurrencyDao
import com.valerii.coinapp.persistence.CurrencyRateDao
import com.valerii.coinapp.repository.DetailRepository
import com.valerii.coinapp.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideDetailRepository(
        currencyLayerClient: CurrencyLayerClient,
        currencyRateDao: CurrencyRateDao
    ): DetailRepository {
        return DetailRepository(currencyLayerClient, currencyRateDao)
    }

    @Provides
    @ViewModelScoped
    fun provideMainRepository(
        currencyLayerClient: CurrencyLayerClient,
        currencyDao: CurrencyDao,
        currencyRateDao: CurrencyRateDao
    ): MainRepository {
        return MainRepository(currencyLayerClient, currencyDao, currencyRateDao)
    }
}