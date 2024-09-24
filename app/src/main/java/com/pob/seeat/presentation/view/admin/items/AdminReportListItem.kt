package com.pob.seeat.presentation.view.admin.items

import com.pob.seeat.data.model.report.ReportedCommentHistoryResponse
import com.pob.seeat.data.model.report.ReportedFeedHistoryResponse
import com.pob.seeat.utils.Utils.toLocalDateTime
import java.time.LocalDateTime

sealed class AdminReportListItem {
    data class CommentReport(
        val content: String,
        val date: LocalDateTime,
        val isDeleted: Boolean,
    ) : AdminReportListItem()

    data class FeedReport(
        val content: String,
        val date: LocalDateTime,
        val isDeleted: Boolean,
    ) : AdminReportListItem()
}

fun List<ReportedCommentHistoryResponse>.toCommentAdminReportListItem() = map {
    AdminReportListItem.CommentReport(
        content = it.content?.comment ?: "",
        date = it.timeStamp?.toLocalDateTime() ?: LocalDateTime.now(),
        isDeleted = it.isDeleted,
    )
}

fun List<ReportedFeedHistoryResponse>.toFeedAdminReportListItem() = map {
    AdminReportListItem.FeedReport(
        content = it.content?.content ?: "",
        date = it.timeStamp?.toLocalDateTime() ?: LocalDateTime.now(),
        isDeleted = it.isDeleted,
    )
}