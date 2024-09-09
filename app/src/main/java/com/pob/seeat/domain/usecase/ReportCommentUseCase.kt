package com.pob.seeat.domain.usecase

import com.google.firebase.Timestamp
import com.pob.seeat.data.repository.ReportCommentRepository
import javax.inject.Inject

class ReportCommentUseCase @Inject constructor(
    private val reportCommentRepository: ReportCommentRepository
) {
    fun execute(reporterId: String, reportedUserId: String, feedId: String, commentId: String,timestamp: Timestamp) {
        reportCommentRepository.reportComment(reporterId, reportedUserId, feedId, commentId,timestamp)
    }
}