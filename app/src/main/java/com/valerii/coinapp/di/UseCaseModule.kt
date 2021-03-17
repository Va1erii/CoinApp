package com.valerii.coinapp.di

import com.valerii.coinapp.model.usecase.BaseCalculateRatesUseCase
import com.valerii.coinapp.model.usecase.CalculateRatesUseCase
import com.valerii.coinapp.utils.BigDecimalValueConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideCalculateRatesUseCase(
        valueConverter: BigDecimalValueConverter,
    ): CalculateRatesUseCase {
        return BaseCalculateRatesUseCase(valueConverter)
    }
}