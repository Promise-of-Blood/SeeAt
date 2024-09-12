package com.pob.seeat.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.pob.seeat.data.model.CommentData
import kotlinx.parcelize.RawValue

data class CommentModel(
    val feedId : String = "",
    val commentId : String = "",
    val user: @RawValue DocumentReference? = null,
    val comment: String = "",
    val likeCount: Int = 0,
    val timeStamp: Timestamp? = null,
    val userImage: String = "",
    val userNickname: String = ""
)

fun CommentModel.toCommentData(): CommentData {
    return CommentData(
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