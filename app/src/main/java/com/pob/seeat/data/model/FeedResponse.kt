package com.pob.seeat.data.model

import java.util.Date

data class FeedResponse (
    val uId: String?,
    val thumbnailUrl: String?,
    val tagName: String?,
    val title: String?,
    val content: String?,
    val commentCount: Int?,
    val likeCount: Int?,
    val username: String?,
    val dateTime: Date?,
)