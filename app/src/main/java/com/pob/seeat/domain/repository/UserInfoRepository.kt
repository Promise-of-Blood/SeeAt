package com.pob.seeat.domain.repository


import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.UserInfoModel
import kotlinx.coroutines.flow.Flow

interface UserInfoRepository {
    suspend fun createUserInfo(userInfo: UserInfoModel)
    suspend fun getUserInfo(uid: String): Flow<UserInfoModel?>
    suspend fun deleteUserInfo(userInfo: UserInfoModel)
    suspend fun updateUserInfo(userInfo: UserInfoModel)
    suspend fun getUserInfoByEmail(email: String): Flow<UserInfoModel?>
    suspend fun getCurrentUserUid(): Flow<String>
    suspend fun createLikedFeed(uid: String, feedUid: String)
    suspend fun removeLikedFeed(uid: String, feedUid: String)
    suspend fun getUserList(): Flow<Result<List<UserInfoModel>>>
}