package com.pob.seeat.domain.model

import CommentModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class FeedModel(
    val feedId: String = "",
    val title: String = "",
    val content: String = "",
    val user: String = "",
    val like: Int = 0,
    val commentsCount: Int = 0,
    val location: GeoPoint? = null,
    val date: Timestamp? = null,
    val comments: List<CommentModel> = emptyList()
)

