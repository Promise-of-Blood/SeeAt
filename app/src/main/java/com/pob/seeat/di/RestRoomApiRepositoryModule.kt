package com.pob.seeat.di

import com.pob.seeat.data.repository.SampleRepositoryImpl
import com.pob.seeat.data.repository.SeoulRestroomApiRepositoryImpl
import com.pob.seeat.domain.repository.RestroomApiRepository
import com.pob.seeat.domain.repository.SampleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RestRoomApiRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSeoulRestroomApiRepository(
        seoulRestroomApiRepositoryImpl: SeoulRestroomApiRepositoryImpl
    ): RestroomApiRepository

}