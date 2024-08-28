package com.pob.seeat.di

import com.pob.seeat.data.mockup.AlarmCacheDataSource
import com.pob.seeat.data.repository.AlarmRepositoryImpl
import com.pob.seeat.domain.repository.AlarmRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAlarmRepository(
        alarmRepositoryImpl: AlarmRepositoryImpl
    ): AlarmRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AlarmCacheDataSourceModule {

    @Provides
    @Singleton
    fun provideAlarmCacheDataSource(): AlarmCacheDataSource {
        return AlarmCacheDataSource
    }
}