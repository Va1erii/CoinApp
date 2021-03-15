package com.valerii.coinapp.di

import com.valerii.coinapp.model.usecase.BaseCalculateRatesUseCase
import com.valerii.coinapp.model.usecase.CalculateRatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Provides
    @Singleton
    fun provideCalculateRatesUseCase(): CalculateRatesUseCase {
        return BaseCalculateRatesUseCase()
    }
}