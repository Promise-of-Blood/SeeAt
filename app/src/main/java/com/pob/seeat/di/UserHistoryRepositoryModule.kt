package com.pob.seeat.di

import com.pob.seeat.data.repository.UserHistoryRepositoryImpl
import com.pob.seeat.domain.repository.UserHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserHistoryRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserHistoryRepository(
        userHistoryRepositoryImpl: UserHistoryRepositoryImpl
    ): UserHistoryRepository
}