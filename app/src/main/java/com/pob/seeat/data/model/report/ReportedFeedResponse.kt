package com.pob.seeat.data.model.report

import com.google.firebase.Timestamp

data class ReportedFeedResponse(
    val feedId: String = "",
    val reportedUserId: String = "",
    val reporterId: String = "",
    val timeStamp: Timestamp? = null,
    val feedTitle: String = "",
    val feedContent: String = "",
)