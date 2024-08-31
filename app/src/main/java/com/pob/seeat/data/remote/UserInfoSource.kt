package com.pob.seeat.data.remote


import com.pob.seeat.data.model.UserInfoData
import kotlinx.coroutines.flow.Flow

interface UserInfoSource {
    suspend fun createUserInfo(userInfo: UserInfoData)
    suspend fun getUserInfo(uid : String) : Flow<UserInfoData?>
    suspend fun deleteUserInfo(userInfo: UserInfoData)
    suspend fun updateUserInfo(userInfo: UserInfoData)
    suspend fun getUserInfoByEmail(email :String) : Flow<UserInfoData?>
}