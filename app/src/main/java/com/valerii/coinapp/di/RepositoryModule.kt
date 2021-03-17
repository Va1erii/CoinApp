package com.valerii.coinapp.di

import com.valerii.coinapp.repository.FakeCurrencyRateGenerator
import com.valerii.coinapp.network.BaseCurrencyLayerClient
import com.valerii.coinapp.network.CurrencyLayerClient
import com.valerii.coinapp.persistence.CurrencyDao
import com.valerii.coinapp.persistence.CurrencyRateDao
import com.valerii.coinapp.repository.BaseFakeCurrencyRateGenerator
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
    fun provideMainRepository(
        currencyLayerClient: CurrencyLayerClient,
        fakeCurrencyRateGenerator: FakeCurrencyRateGenerator,
        currencyDao: CurrencyDao,
        currencyRateDao: CurrencyRateDao
    ): MainRepository {
        return MainRepository(
            currencyLayerClient,
            fakeCurrencyRateGenerator,
            currencyDao,
            currencyRateDao
        )
    }

    @Provides
    @ViewModelScoped
    fun provideFakeCurrencyRateGenerator(): FakeCurrencyRateGenerator {
        return BaseFakeCurrencyRateGenerator()
    }
}