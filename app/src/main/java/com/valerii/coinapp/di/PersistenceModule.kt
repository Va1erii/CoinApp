package com.valerii.coinapp.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.valerii.coinapp.persistence.AppDatabase
import com.valerii.coinapp.persistence.CurrencyDao
import com.valerii.coinapp.persistence.CurrencyRateDao
import com.valerii.coinapp.persistence.converter.QuoteConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PersistenceModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application,
        quoteConverter: QuoteConverter
    ): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, "CoinApp.db")
            .fallbackToDestructiveMigration()
            .addTypeConverter(quoteConverter)
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(appDatabase: AppDatabase): CurrencyDao {
        return appDatabase.currencyDao()
    }

    @Provides
    @Singleton
    fun provideCurrencyRateDao(appDatabase: AppDatabase): CurrencyRateDao {
        return appDatabase.currencyRateDao()
    }

    @Provides
    @Singleton
    fun provideQuoteConverter(moshi: Moshi): QuoteConverter {
        return QuoteConverter(moshi)
    }
}