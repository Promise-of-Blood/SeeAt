package com.pob.seeat.di

import com.pob.seeat.presentation.service.NaverMapWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NaverMapModule {

    @Provides
    @Singleton
    fun provideNaverMapWrapper(): NaverMapWrapper {
        return NaverMapWrapper()
    }
}