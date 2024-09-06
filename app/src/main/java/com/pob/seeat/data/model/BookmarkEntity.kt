package com.pob.seeat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "bookmark_table")
data class BookmarkEntity(
    @PrimaryKey
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