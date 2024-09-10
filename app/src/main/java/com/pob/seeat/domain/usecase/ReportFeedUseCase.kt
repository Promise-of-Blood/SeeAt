package com.pob.seeat.domain.usecase

import com.pob.seeat.domain.model.FeedReportModel
import com.pob.seeat.domain.repository.ReportFeedRepository
import javax.inject.Inject

class ReportFeedUseCase @Inject constructor(private val reportFeedRepository: ReportFeedRepository) {
    suspend operator fun invoke(
        feedReportModel: FeedReportModel
    ){
        return reportFeedRepository.addFeed(feedReportModel)
    }
}
