package com.pob.seeat.domain.model

import com.pob.seeat.data.model.BookmarkEntity
import java.time.LocalDateTime

data class BookmarkModel(
    val feedId: String,
    val nickname: String,
    val title: String,
    val content: String,
    val like: Int,
    val commentsCount: Int,
    val date: LocalDateTime,
    val tags: List<String>,
    val contentImage: String,
    val isLiked: Boolean,
)

fun List<BookmarkEntity>.toBookmarkList() = this.map {
    BookmarkModel(
        feedId = it.feedId,
        nickname = it.nickname,
        title = it.title,
        content = it.content,
        like = it.like,
        commentsCount = it.commentsCount,
        date = it.date,
        tags = it.tags,
        contentImage = it.contentImage,
        isLiked = it.isLiked,
    )
}

fun BookmarkModel.toBookmarkEntity() = BookmarkEntity(
    feedId = this.feedId,
    nickname = this.nickname,
    title = this.title,
    content = this.content,
    like = this.like,
    commentsCount = this.commentsCount,
    date = this.date,
    tags = this.tags,
    contentImage = this.contentImage,
    isLiked = this.isLiked,
)