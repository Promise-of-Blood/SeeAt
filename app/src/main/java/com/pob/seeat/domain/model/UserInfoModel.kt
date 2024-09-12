package com.pob.seeat.domain.model

import com.pob.seeat.data.model.UserInfoData

data class UserInfoModel(
    val uid: String = "",
    val email: String = "",
    val nickname: String = "",
    val profileUrl: String = "",
    val introduce: String = "",
    val token: String = "",
    val feedCount: Long = 0,
    val commentCount: Long = 0,
    val likedFeedList: List<String> = emptyList()
)


fun UserInfoModel.toUserInfo(): UserInfoData {
    return UserInfoData(
        uid = this.uid,
        email = this.email,
        nickname = this.nickname,
        profileUrl = this.profileUrl,
        introduce = this.introduce,
        token = this.token,
        feedCount = this.feedCount,
        commentCount = this.commentCount,
        likedFeedList = this.likedFeedList
    )
}