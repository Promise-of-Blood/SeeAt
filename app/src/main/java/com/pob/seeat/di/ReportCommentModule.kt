package com.pob.seeat.di

import com.pob.seeat.data.remote.ReportCommentService
import com.pob.seeat.data.repository.ReportCommentRepository
import com.pob.seeat.domain.repository.ReportCommentRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ReportCommentModule {

    @Provides
    fun provideReportCommentRepository(
        reportCommentService: ReportCommentService
    ): ReportCommentRepository{
        return ReportCommentRepositoryImpl(reportCommentService)
    }
}