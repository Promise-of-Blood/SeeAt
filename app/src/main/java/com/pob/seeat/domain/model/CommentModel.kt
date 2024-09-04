package com.pob.seeat.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import kotlinx.parcelize.RawValue

data class CommentModel(
    val commentId: String = "",
    val user: @RawValue DocumentReference? = null,
    val comment: String = "",
    val likeCount: Int = 0,
    val timeStamp: Timestamp? = null,
    val userImage: String = "",
    val userNickname: String = ""
)