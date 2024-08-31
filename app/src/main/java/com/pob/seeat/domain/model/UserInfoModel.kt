package com.pob.seeat.domain.model

import com.pob.seeat.data.model.UserInfoData
import com.pob.seeat.data.model.toUserInfoModel

data class UserInfoModel(
    val uid : String = "",
    val email : String = "",
    val nickname : String = "",
    val profileUrl : String = "",
    val introduce : String = ""
)



fun UserInfoModel.toUserInfo(): UserInfoData{
    return UserInfoData(
        uid = this.uid,
        email = this.email,
        nickname = this.nickname,
        profileUrl = this.profileUrl,
        introduce = this.introduce
    )
}