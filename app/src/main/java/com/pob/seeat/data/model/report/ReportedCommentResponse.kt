package com.pob.seeat.data.model.report

import com.google.firebase.Timestamp

data class ReportedCommentResponse(
    val commentId: String = "",
    val feedId: String = "",
    val reportedUserId: String = "",
    val reporterId: String = "",
    val timeStamp: Timestamp? = null,
    val comment: String = "",
)