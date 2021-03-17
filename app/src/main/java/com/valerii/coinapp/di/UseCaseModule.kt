package com.valerii.coinapp.di

import com.valerii.coinapp.model.usecase.BaseCalculateRatesUseCase
import com.valerii.coinapp.model.usecase.CalculateRatesUseCase
import com.valerii.coinapp.utils.ValueConverter
import com.valerii.coinapp.utils.ValueFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import java.math.BigDecimal

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideCalculateRatesUseCase(
        valueConverter: ValueConverter<BigDecimal>,
        valueFormatter: ValueFormatter<BigDecimal>,
    ): CalculateRatesUseCase {
        return BaseCalculateRatesUseCase(valueConverter, valueFormatter)
    }
}