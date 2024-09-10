package com.pob.seeat.data.remote

import com.pob.seeat.domain.model.FeedReportModel

interface SendReportFeed {
    suspend fun addReportFeed(report: FeedReportModel)
}
