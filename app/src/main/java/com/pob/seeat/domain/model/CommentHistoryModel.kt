package com.pob.seeat.domain.model

import com.google.firebase.Timestamp

data class CommentHistoryModel(
    val feedId: String = "",
    val feedTitle: String = "",
    val commentId: String = "",
    val comment: String = "",
    val likeCount: Int = 0,
    val timeStamp: Timestamp? = null,
)
