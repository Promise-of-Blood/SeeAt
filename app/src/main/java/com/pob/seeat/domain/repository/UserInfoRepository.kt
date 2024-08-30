package com.pob.seeat.domain.repository


import com.pob.seeat.domain.model.UserInfoModel
import kotlinx.coroutines.flow.Flow

interface UserInfoRepository {
    suspend fun createUserInfo(userInfo : UserInfoModel)
    suspend fun getUserInfo(uid : String) : Flow<UserInfoModel?>
    suspend fun deleteUserInfo(userInfo: UserInfoModel)
    suspend fun updateUserInfo(userInfo: UserInfoModel)
}