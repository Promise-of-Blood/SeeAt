package com.pob.seeat.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CommentModel(
    val commentId: String = "",
    val user: @RawValue DocumentReference? = null,
    val comment: String = "",
    val likeCount: Int = 0,
    val timeStamp: Timestamp? = null,
) : Parcelable