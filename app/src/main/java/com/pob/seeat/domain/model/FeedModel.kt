package com.pob.seeat.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class FeedModel(
    val feedId: String = "",
    val user: DocumentReference? = null,
    val nickname: String = "",
    val title: String = "",
    val content: String = "",
    val like: Int = 0,
    val commentsCount: Int = 0,
    val location: GeoPoint? = null,
    val date: Timestamp? = null,
    val comments: List<CommentModel> = emptyList(),
    val tags: List<String> = emptyList(),
    val userImage: String = ""
)

