package com.pob.seeat.presentation.view.admin.items

import com.pob.seeat.data.model.CommentData
import com.pob.seeat.data.model.report.ReportedCommentHistoryResponse
import com.pob.seeat.data.model.report.ReportedFeedHistoryResponse
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.utils.Utils.toLocalDateTime
import java.time.LocalDateTime

sealed class AdminReportListItem {
    data class CommentReport(
        val content: CommentData?,
        val date: LocalDateTime,
        val isDeleted: Boolean,
        val reportedCount: Long,
    ) : AdminReportListItem()

    data class FeedReport(
        val content: FeedModel?,
        val date: LocalDateTime,
        val isDeleted: Boolean,
        val reportedCount: Long,
    ) : AdminReportListItem()
}

fun List<ReportedCommentHistoryResponse>.toCommentAdminReportListItem() = map {
    AdminReportListItem.CommentReport(
        content = it.content,
        date = it.timeStamp?.toLocalDateTime() ?: LocalDateTime.now(),
        isDeleted = it.isDeleted,
        reportedCount = it.reportedCount,
    )
}

fun List<ReportedFeedHistoryResponse>.toFeedAdminReportListItem() = map {
    AdminReportListItem.FeedReport(
        content = it.content,
        date = it.timeStamp?.toLocalDateTime() ?: LocalDateTime.now(),
        isDeleted = it.isDeleted,
        reportedCount = it.reportedCount,
    )
}