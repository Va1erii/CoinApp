package com.valerii.coinapp.di

import com.valerii.coinapp.utils.BigDecimalValueConverter
import com.valerii.coinapp.utils.BigDecimalValueFormatter
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
class UtilsModule {
    @Provides
    @ViewModelScoped
    fun provideValueConverter(): ValueConverter<BigDecimal> {
        return BigDecimalValueConverter()
    }

    @Provides
    @ViewModelScoped
    fun provideValueFormatter(): ValueFormatter<BigDecimal> {
        return BigDecimalValueFormatter()
    }
}