package com.pob.seeat.domain.model

import com.google.firebase.Timestamp

data class CommentModel(
    val commentId: String = "",
    val user: String = "",
    val comment: String = "",
    val likeCount: Int = 0,
    val timeStamp: Timestamp? = null,
)