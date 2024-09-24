package com.pob.seeat.domain.usecase

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.report.ReportedCommentHistoryResponse
import com.pob.seeat.data.model.report.ReportedFeedHistoryResponse
import com.pob.seeat.domain.model.ReportedCommentModel
import com.pob.seeat.domain.model.ReportedFeedModel
import com.pob.seeat.domain.model.ReportedInfoModel
import com.pob.seeat.domain.repository.ReportCommentRepository
import com.pob.seeat.domain.repository.ReportFeedRepository
import com.pob.seeat.utils.Utils.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportedCommentListUseCase @Inject constructor(
    private val reportCommentRepository: ReportCommentRepository,
) {
    suspend operator fun invoke(): Flow<Result<List<ReportedCommentModel>>> {
        return reportCommentRepository.getReportedCommentList().map { response ->
            when (response) {
                is Result.Success -> {
                    val groupByCommentId = response.data.groupBy { it.commentId }
                    val result = groupByCommentId.mapNotNull { (commentId, reportedList) ->
                        val reportList = reportedList.mapNotNull { report ->
                            val date = report.timeStamp?.toLocalDateTime()
                            if (date == null) null
                            else ReportedInfoModel(
                                reporterId = report.reporterId,
                                reportedAt = date,
                            )
                        }
                        ReportedCommentModel(
                            feedId = reportedList.first().feedId,
                            commentId = commentId,
                            comment = reportedList.first().comment,
                            reportedUserId = reportedList.first().reportedUserId,
                            reportList = reportList
                        )
                    }
                    Result.Success(result)
                }

                is Result.Error -> Result.Error(response.message)
                is Result.Loading -> Result.Loading
            }
        }
    }
}

class ReportedCommentHistoryListUseCase @Inject constructor(
    private val reportCommentRepository: ReportCommentRepository,
) {
    suspend operator fun invoke(uid: String): Flow<Result<List<ReportedCommentHistoryResponse>>> {
        return reportCommentRepository.getReportedCommentList(uid)
    }
}

class ReportedFeedListUseCase @Inject constructor(
    private val reportFeedRepository: ReportFeedRepository,
) {
    suspend operator fun invoke(): Flow<Result<List<ReportedFeedModel>>> {
        return reportFeedRepository.getReportedFeedList().map { response ->
            when (response) {
                is Result.Success -> {
                    val groupByFeedId = response.data.groupBy { it.feedId }
                    val result = groupByFeedId.map { (feedId, reportedList) ->
                        val reportList = reportedList.mapNotNull { report ->
                            val date = report.timeStamp?.toLocalDateTime()
                            if (date == null) null
                            else ReportedInfoModel(
                                reporterId = report.reporterId,
                                reportedAt = date,
                            )
                        }
                        ReportedFeedModel(
                            feedId = feedId,
                            feedTitle = reportedList.first().feedTitle,
                            feedContent = reportedList.first().feedContent,
                            reportedUserId = reportedList.first().reportedUserId,
                            reportList = reportList,
                        )
                    }
                    Result.Success(result)
                }

                is Result.Error -> Result.Error(response.message)
                is Result.Loading -> Result.Loading
            }
        }
    }
}

class ReportedFeedHistoryListUseCase @Inject constructor(
    private val reportFeedRepository: ReportFeedRepository,
) {
    suspend operator fun invoke(uid: String): Flow<Result<List<ReportedFeedHistoryResponse>>> {
        return reportFeedRepository.getReportedFeedList(uid)
    }
}

class DeleteReportedCommentUseCase @Inject constructor(
    private val reportCommentRepository: ReportCommentRepository,
) {
    suspend operator fun invoke(feedId: String, commentId: String) {
        reportCommentRepository.deleteReportedComment(feedId, commentId)
    }
}

class DeleteReportedFeedUseCase @Inject constructor(
    private val reportFeedRepository: ReportFeedRepository,
) {
    suspend operator fun invoke(feedId: String) {
        reportFeedRepository.deleteReportedFeed(feedId)
    }
}

class DeleteReportByCommentIdUseCase @Inject constructor(
    private val reportCommentRepository: ReportCommentRepository,
) {
    suspend operator fun invoke(commentId: String) {
        reportCommentRepository.deleteReportByCommentId(commentId)
    }
}

class DeleteReportByFeedIdUseCase @Inject constructor(
    private val reportFeedRepository: ReportFeedRepository,
) {
    suspend operator fun invoke(feedId: String) {
        reportFeedRepository.deleteReportByFeedId(feedId)
    }
}