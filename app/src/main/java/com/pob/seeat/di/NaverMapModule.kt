package com.pob.seeat.di

import com.naver.maps.map.NaverMap
import com.pob.seeat.data.repository.NaverMapWrapper
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