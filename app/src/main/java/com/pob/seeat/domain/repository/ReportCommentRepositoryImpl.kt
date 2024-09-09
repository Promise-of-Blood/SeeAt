package com.pob.seeat.domain.repository

import com.google.firebase.Timestamp
import com.pob.seeat.data.remote.ReportCommentService
import com.pob.seeat.data.repository.ReportCommentRepository
import com.pob.seeat.domain.model.CommentReportModel
import javax.inject.Inject

class ReportCommentRepositoryImpl @Inject constructor(
    private val reportCommentService: ReportCommentService
) : ReportCommentRepository {
    override fun reportComment(
        reporterId: String,
        reportedUserId: String,
        feedId: String,
        commentId: String,
        timestamp: Timestamp
    ) {
        val report = CommentReportModel(reporterId, reportedUserId, feedId, commentId,timestamp)
        reportCommentService.sendReportComment(report)
    }
}