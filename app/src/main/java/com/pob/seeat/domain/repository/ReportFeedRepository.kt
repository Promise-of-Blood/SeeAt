package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.report.ReportedFeedResponse
import com.pob.seeat.domain.model.FeedReportModel
import kotlinx.coroutines.flow.Flow

interface ReportFeedRepository {
    suspend fun addFeed(reportModel: FeedReportModel)
    suspend fun getReportedFeedList(): Flow<Result<List<ReportedFeedResponse>>>
    suspend fun deleteReportedFeed(feedId: String)
    suspend fun deleteReportByFeedId(feedId: String)
}
