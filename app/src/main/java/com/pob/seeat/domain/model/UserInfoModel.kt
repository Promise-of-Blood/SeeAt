package com.pob.seeat.domain.model

import com.pob.seeat.data.model.UserInfoData

data class UserInfoModel(
    val uid : String = "",
    val email : String = "",
    val nickname : String = ""
)



fun UserInfoModel.toUserInfo(): UserInfoData{
    return UserInfoData(
        uid = this.uid,
        email = this.email,
        nickname = this.nickname
    )
}