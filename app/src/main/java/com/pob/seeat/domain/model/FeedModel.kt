package com.pob.seeat.domain.model

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
    val comments: List<CommentModel> = emptyList(),
    val tags: List<String> = emptyList()
)

fun List<FeedResponse>.toFeedModelList() = this.map { item ->
    FeedModel(
        uId = item.uId ?: "",
        thumbnailUrl = item.thumbnailUrl ?: "",
        tagName = item.tagName ?: "",
        title = item.title ?: "",
        content = item.content ?: "",
        commentCount = item.commentCount ?: 0,
        likeCount = item.likeCount ?: 0,
        username = item.username ?: "",
        dateTime = item.dateTime ?: Date()
    )
}

