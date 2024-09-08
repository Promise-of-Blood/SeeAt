package com.pob.seeat.domain.model

import com.google.firebase.Timestamp
import com.pob.seeat.data.model.BookmarkEntity
import java.util.Date

data class BookmarkModel(
    val feedId: String,
    val nickname: String,
    val title: String,
    val content: String,
    val like: Int,
    val commentsCount: Int,
    val date: Timestamp,
    val tags: List<String>,
    val contentImage: String,
)

fun List<BookmarkEntity>.toBookmarkList() = this.map {
    BookmarkModel(
        feedId = it.feedId,
        nickname = it.nickname,
        title = it.title,
        content = it.content,
        like = it.like,
        commentsCount = it.commentsCount,
        date = Timestamp(it.date),
        tags = it.tags,
        contentImage = it.contentImage,
    )
}

fun BookmarkModel.toBookmarkEntity() = BookmarkEntity(
    feedId = this.feedId,
    nickname = this.nickname,
    title = this.title,
    content = this.content,
    like = this.like,
    commentsCount = this.commentsCount,
    date = this.date.toDate(),
    tags = this.tags,
    contentImage = this.contentImage,
)

fun FeedModel.toBookmarkEntity() = BookmarkEntity(
    feedId = this.feedId,
    nickname = this.nickname,
    title = this.title,
    content = this.content,
    like = this.like,
    commentsCount = this.commentsCount,
    date = this.date?.toDate() ?: Date(),
    tags = this.tags,
    contentImage = this.contentImage.firstOrNull() ?: "",
)

fun List<FeedModel>.toBookmarkModelList() = this.map {
    BookmarkModel(
        feedId = it.feedId,
        nickname = it.nickname,
        title = it.title,
        content = it.content,
        like = it.like,
        commentsCount = it.commentsCount,
        date = it.date ?: Timestamp.now(),
        tags = it.tags,
        contentImage = it.contentImage.firstOrNull() ?: "",
    )
}