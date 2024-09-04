package com.pob.seeat.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.pob.seeat.domain.model.CommentModel
import kotlinx.parcelize.RawValue

data class CommentData(
    val feedId: String = "",
    val commentId: String,
    val user: @RawValue DocumentReference? = null,
    val comment: String = "",
    val likeCount: Int = 0,
    val timeStamp: Timestamp? = null,
    val userImage: String = "",
    val userNickname: String = ""
){
    constructor() : this(
        feedId = "",
        commentId = "",
        user = null,
        comment = "",
        likeCount = 0,
        timeStamp = null,
        userImage = "",
        userNickname = ""
    )
}

fun CommentData.toCommentModel(): CommentModel {
    return CommentModel(
        feedId = this.feedId,
        commentId = this.commentId,
        user = this.user,
        comment = this.comment,
        likeCount = this.likeCount,
        timeStamp = this.timeStamp,
        userImage = this.userImage,
        userNickname = this.userNickname
    )
}