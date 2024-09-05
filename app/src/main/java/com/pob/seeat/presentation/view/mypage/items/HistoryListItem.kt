package com.pob.seeat.presentation.view.mypage.items

import com.pob.seeat.domain.model.CommentHistoryModel
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime

sealed class HistoryListItem {
    data class FeedItem(
        val feedId: String,
        val tagList: List<String>,
        val title: String,
        val content: String,
        val commentCount: Int,
        val likeCount: Int,
        val time: String,
        val image: String,
        val viewType: HistoryEnum = HistoryEnum.FEED,
    ) : HistoryListItem()

    data class CommentItem(
        val feedId: String,
        val feedTitle: String,
        val commentId: String,
        val comment: String,
        val likeCount: Int,
        val time: String,
        val viewType: HistoryEnum = HistoryEnum.COMMENT,
    ) : HistoryListItem()
}

fun List<FeedModel>.toHistoryListFeedItemList(): List<HistoryListItem> {
    return this.map {
        HistoryListItem.FeedItem(
            feedId = it.feedId,
            tagList = it.tags.orEmpty(),
            title = it.title,
            content = it.content,
            commentCount = it.commentsCount,
            likeCount = it.like,
            time = it.date?.toLocalDateTime()?.toKoreanDiffString() ?: "",
            image = it.contentImage.firstOrNull() ?: "",
        )
    }
}

fun List<CommentHistoryModel>.toHistoryListCommentItemList(): List<HistoryListItem> {
    return this.map { comment ->
        HistoryListItem.CommentItem(
            feedId = comment.feedId,
            feedTitle = comment.feedTitle,
            commentId = comment.commentId,
            comment = comment.comment,
            likeCount = comment.likeCount,
            time = comment.timeStamp?.toLocalDateTime()?.toKoreanDiffString() ?: "",
        )
    }
}
