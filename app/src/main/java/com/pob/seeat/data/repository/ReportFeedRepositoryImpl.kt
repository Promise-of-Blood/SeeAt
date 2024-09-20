package com.pob.seeat.data.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.report.ReportedFeedResponse
import com.pob.seeat.data.remote.ReportFeedRemote
import com.pob.seeat.domain.model.FeedReportModel
import com.pob.seeat.domain.repository.ReportFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class ReportFeedRepositoryImpl @Inject constructor(private val reportFeedRemote: ReportFeedRemote) :
    ReportFeedRepository {
    override suspend fun addFeed(reportModel: FeedReportModel) {
        try {
            reportFeedRemote.addReportFeed(reportModel)
        } catch (e: Exception) {
            Timber.i(e.toString())
        }
    }

    override suspend fun getReportedFeedList(): Flow<Result<List<ReportedFeedResponse>>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(reportFeedRemote.getReportedFeedList()))
        } catch (e: Exception) {
            Timber.tag("게시물 신고").e(e.toString())
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }
}