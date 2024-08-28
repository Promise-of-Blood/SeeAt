package com.pob.seeat.di

import com.pob.seeat.domain.repository.SampleRepository
import com.pob.seeat.domain.usecase.GetSampleImageListUseCase
import com.pob.seeat.domain.usecase.GetSampleVideoListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetSampleImageListUseCase(repository: SampleRepository): GetSampleImageListUseCase {
        return GetSampleImageListUseCase(repository)
    }
    @Provides
    fun provideGetSampleVideoListUseCase(repository: SampleRepository): GetSampleVideoListUseCase{
        return GetSampleVideoListUseCase(repository)
    }
}