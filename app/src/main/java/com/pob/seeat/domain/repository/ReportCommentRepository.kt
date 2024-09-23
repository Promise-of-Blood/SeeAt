package com.pob.seeat.domain.repository

import com.google.firebase.Timestamp
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.report.ReportedCommentResponse
import kotlinx.coroutines.flow.Flow

interface ReportCommentRepository {
    fun reportComment(
        reporterId: String,
        reportedUserId: String,
        feedId: String,
        commentId: String,
        timestamp: Timestamp
    )

    suspend fun getReportedCommentList(): Flow<Result<List<ReportedCommentResponse>>>
    suspend fun deleteReportedComment(feedId: String, commentId: String)
    suspend fun deleteReportByCommentId(commentId: String)
}