package com.pob.seeat.domain.model

import com.pob.seeat.data.model.FeedResponse
import java.util.Date
import java.util.UUID

data class FeedModel(
    val uId: String,
    val thumbnailUrl: String,
    val tagName: String,
    val title: String,
    val content: String,
    val commentCount: Int,
    val likeCount: Int,
    val username: String,
    val dateTime: Date,
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

