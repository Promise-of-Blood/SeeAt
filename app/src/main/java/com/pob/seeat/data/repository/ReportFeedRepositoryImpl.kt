package com.pob.seeat.data.repository

import com.pob.seeat.data.remote.ReportFeedRemote
import com.pob.seeat.domain.model.FeedReportModel
import com.pob.seeat.domain.repository.ReportFeedRepository
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
}