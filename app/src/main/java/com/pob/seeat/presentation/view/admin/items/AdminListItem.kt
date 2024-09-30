package com.pob.seeat.presentation.view.admin.items

import com.pob.seeat.domain.model.ReportedCommentModel
import com.pob.seeat.domain.model.ReportedFeedModel
import com.pob.seeat.domain.model.UserInfoModel
import java.time.LocalDateTime

sealed class AdminListItem {
    data class User(
        val uid: String,
        val nickname: String,
        val email: String,
        val isAdmin: Boolean,
        val profileImage: String,
        val viewType: AdminEnum = AdminEnum.USER,
    ) : AdminListItem()

    data class FeedReport(
        val feedId: String,
        val feedTitle: String,
        val feedContent: String,
        val reportCount: Int,
        val recentReportedAt: LocalDateTime,
        val viewType: AdminEnum = AdminEnum.FEED_REPORT,
    ) : AdminListItem()

    data class CommentReport(
        val feedId: String,
        val commentId: String,
        val comment: String,
        val reportCount: Int,
        val recentReportedAt: LocalDateTime,
        val viewType: AdminEnum = AdminEnum.COMMENT_REPORT,
    ) : AdminListItem()
}

fun List<ReportedFeedModel>.toFeedAdminListItem() = map {
    AdminListItem.FeedReport(
        feedId = it.feedId,
        feedTitle = it.feedTitle,
        feedContent = it.feedContent,
        reportCount = it.reportList.size,
        recentReportedAt = it.reportList.first().reportedAt,
    )
}

fun List<ReportedCommentModel>.toCommentAdminListItem() = map {
    AdminListItem.CommentReport(
        feedId = it.feedId,
        commentId = it.commentId,
        comment = it.comment,
        reportCount = it.reportList.size,
        recentReportedAt = it.reportList.first().reportedAt,
    )
}

fun List<UserInfoModel>.toUserAdminListItem() = map {
    AdminListItem.User(
        uid = it.uid,
        nickname = it.nickname,
        email = it.email,
        isAdmin = it.isAdmin,
        profileImage = it.profileUrl,
    )
}
