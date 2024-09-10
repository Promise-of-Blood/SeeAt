package com.pob.seeat.domain.repository

import com.pob.seeat.domain.model.FeedReportModel

interface ReportFeedRepository {
    suspend fun addFeed(reportModel: FeedReportModel)
}
