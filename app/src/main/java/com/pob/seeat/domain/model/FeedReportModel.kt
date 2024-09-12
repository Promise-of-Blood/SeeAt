package com.pob.seeat.domain.model

import com.google.firebase.Timestamp

data class FeedReportModel(
    val reporterId: String? = "",
    val reportedUserId: String? = "",
    val feedId: String = "",
    val timeStamp: Timestamp? = null
)