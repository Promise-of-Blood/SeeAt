package com.pob.seeat.presentation.view.admin.items

import com.pob.seeat.domain.model.ReportedCommentModel
import com.pob.seeat.domain.model.ReportedFeedModel

sealed class AdminListItem {
    data class User(
        val uid: String,
        val nickname: String,
        val email: String,
        val isAdmin: String,
        val profileImage: String,
        val viewType: AdminEnum = AdminEnum.USER,
    ) : AdminListItem()

    data class FeedReport(
        val feedId: String,
        val feedTitle: String,
        val feedContent: String,
        val reportCount: Int,
        val viewType: AdminEnum = AdminEnum.FEED_REPORT,
    ) : AdminListItem()

    data class CommentReport(
        val commentId: String,
        val comment: String,
        val reportCount: Int,
        val viewType: AdminEnum = AdminEnum.COMMENT_REPORT,
    ) : AdminListItem()
}

fun List<ReportedFeedModel>.toFeedAdminListItem() = map {
    AdminListItem.FeedReport(
        feedId = it.feedId,
        feedTitle = it.feedTitle,
        feedContent = it.feedContent,
        reportCount = it.reportList.size,
    )
}

fun List<ReportedCommentModel>.toCommentAdminListItem() = map {
    AdminListItem.CommentReport(
        commentId = it.commentId,
        comment = it.comment ?: "",
        reportCount = it.reportList.size,
    )
}