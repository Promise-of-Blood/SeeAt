package com.pob.seeat.data.repository

import com.google.firebase.Timestamp
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.report.ReportedCommentHistoryResponse
import com.pob.seeat.data.model.report.ReportedCommentResponse
import com.pob.seeat.data.remote.ReportCommentService
import com.pob.seeat.domain.model.CommentReportModel
import com.pob.seeat.domain.repository.ReportCommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
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
        val report = CommentReportModel(reporterId, reportedUserId, feedId, commentId, timestamp)
        reportCommentService.sendReportComment(report)
    }

    override suspend fun getReportedCommentList(): Flow<Result<List<ReportedCommentResponse>>> =
        flow {
            emit(Result.Loading)
            try {
                emit(Result.Success(reportCommentService.getReportedCommentList()))
            } catch (e: Exception) {
                Timber.tag("댓글 신고").e(e.toString())
                emit(Result.Error(e.message ?: "An unknown error occurred"))
            }
        }

    override suspend fun getReportedCommentList(uid: String): Flow<Result<List<ReportedCommentHistoryResponse>>> =
        flow {
            emit(Result.Loading)
            try {
                val deletedList = reportCommentService.getReportedCommentHistoryList(uid)
                val reportedList = reportCommentService.getReportedCommentList(uid)
                val combinedList = deletedList.plus(reportedList).sortedBy { it.timeStamp }
                emit(Result.Success(combinedList))
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "An unknown error occurred"))
            }
        }

    override suspend fun deleteReportedComment(feedId: String, commentId: String) {
        try {
            reportCommentService.deleteReportedComment(feedId, commentId)
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }

    override suspend fun deleteReportByCommentId(commentId: String) {
        try {
            reportCommentService.deleteReportByCommentId(commentId)
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }
}