package com.pob.seeat.domain.model

import java.time.LocalDateTime

data class ReportedCommentModel(
    val feedId: String = "",
    val commentId: String = "",
    val comment: String = "",
    val reportedUserId: String = "",
    val reportList: List<ReportedInfoModel>,
)

data class ReportedFeedModel(
    val feedId: String = "",
    val feedTitle: String = "",
    val feedContent: String = "",
    val reportedUserId: String = "",
    val reportList: List<ReportedInfoModel>,
)

data class ReportedInfoModel(
    val reporterId: String,
    val reportedAt: LocalDateTime,
)