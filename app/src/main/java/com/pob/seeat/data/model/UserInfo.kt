package com.pob.seeat.data.model

import com.pob.seeat.domain.model.UserInfoModel

data class UserInfoData(
    val uid : String = "",
    val email : String = "",
    val nickname : String = ""
)

fun UserInfoData.toUserInfoModel(): UserInfoModel {
    return UserInfoModel(
        uid = this.uid,
        email = this.email,
        nickname = this.nickname
    )
}