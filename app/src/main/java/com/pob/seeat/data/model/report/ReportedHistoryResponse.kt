package com.pob.seeat.data.model.report

import com.google.firebase.Timestamp
import com.pob.seeat.data.model.CommentData
import com.pob.seeat.domain.model.FeedModel

data class ReportedCommentHistoryResponse(
    val content: CommentData? = null,
    val timeStamp: Timestamp? = null, // 신고 처리 날짜
    val reportedCount: Long = 0, // 신고 횟수
    val isDeleted: Boolean = false, // 삭제 여부
)

data class ReportedFeedHistoryResponse(
    val content: FeedModel? = null,
    val timeStamp: Timestamp? = null, // 신고 처리 날짜
    val reportedCount: Long = 0, // 신고 횟수
    val isDeleted: Boolean = false, // 삭제 여부
)
