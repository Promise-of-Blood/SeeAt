package com.pob.seeat.presentation.view.mypage.items

import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.utils.Utils.toKoreanDiffString
import com.pob.seeat.utils.Utils.toLocalDateTime

sealed class HistoryListItem {
    data class FeedItem(
        val uId: String,
        val tagList: List<String>,
        val title: String,
        val content: String,
        val commentCount: Int,
        val likeCount: Int,
        val time: String,
        val image: String,
        val viewType: HistoryEnum
    ) : HistoryListItem()
}

fun List<FeedModel>.toHistoryListItemList(): List<HistoryListItem> {
    return this.map {
        HistoryListItem.FeedItem(
            uId = it.feedId,
            tagList = it.tags.orEmpty(),
            title = it.title,
            content = it.content,
            commentCount = it.commentsCount,
            likeCount = it.like,
            time = it.date?.toLocalDateTime()?.toKoreanDiffString() ?: "",
            image = "",
            viewType = HistoryEnum.FEED
        )
    }
}
