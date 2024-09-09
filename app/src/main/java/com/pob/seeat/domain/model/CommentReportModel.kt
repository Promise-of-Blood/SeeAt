package com.pob.seeat.domain.model

import com.google.firebase.Timestamp

data class CommentReportModel(
    val reporterId: String = "",
    val reportedUserId: String = "",
    val feedId: String = "",
    val commentId: String = "",
    val timeStamp: Timestamp? = null
)

