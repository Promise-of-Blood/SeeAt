package com.pob.seeat.di

import com.pob.seeat.domain.repository.FeedRepository
import com.pob.seeat.data.repository.FeedRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeedRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFeedRepository(
        feedRepositoryImpl: FeedRepositoryImpl
    ): FeedRepository
}