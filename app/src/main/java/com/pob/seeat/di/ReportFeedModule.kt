package com.pob.seeat.di

import com.pob.seeat.data.repository.ReportFeedRepositoryImpl
import com.pob.seeat.domain.repository.FeedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class ReportFeedModule {

    @Binds
    @Singleton
    abstract fun bindReportFeedRepository(
        reportFeedRepositoryImpl: ReportFeedRepositoryImpl
    ): FeedRepository
}


