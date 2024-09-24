package com.pob.seeat.data.model

import com.pob.seeat.domain.model.UserInfoModel

data class UserInfoData(
    val uid: String = "",
    val email: String = "",
    val nickname: String = "",
    val profileUrl: String = "",
    val introduce: String = "",
    val token: String = "",
    val feedCount: Long = 0,
    val commentCount: Long = 0,
    val likedFeedList: List<String> = emptyList(),
    val isAdmin: Boolean = false,
    val reportedCount: Long = 0,
)

fun UserInfoData.toUserInfoModel(): UserInfoModel {
    return UserInfoModel(
        uid = this.uid,
        email = this.email,
        nickname = this.nickname,
        profileUrl = this.profileUrl,
        introduce = this.introduce,
        token = this.token,
        feedCount = this.feedCount,
        commentCount = this.commentCount,
        likedFeedList = this.likedFeedList,
        isAdmin = this.isAdmin,
        reportedCount = this.reportedCount,
    )
}