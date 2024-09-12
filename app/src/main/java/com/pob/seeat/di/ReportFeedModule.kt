package com.pob.seeat.di

import com.pob.seeat.data.repository.ReportFeedRepositoryImpl
import com.pob.seeat.domain.repository.ReportFeedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ReportFeedModule {

    @Binds
    @ViewModelScoped
    abstract fun bindReportFeedRepository(
        reportFeedRepositoryImpl: ReportFeedRepositoryImpl
    ): ReportFeedRepository
}


