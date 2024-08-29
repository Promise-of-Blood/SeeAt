package com.pob.seeat.domain.model

import java.util.Date
import java.util.UUID

data class PostModel(
    val uId: String = UUID.randomUUID().toString(),
    val thumbnailUrl: String?,
    val tagName: String?,
    val title: String?,
    val content: String?,
    val commentCount: Int?,
    val likeCount: Int?,
    val username: String?,
    val isBookmark: Boolean = false,
    val dateTime: Date?,
)

