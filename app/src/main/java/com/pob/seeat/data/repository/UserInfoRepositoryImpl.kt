package com.pob.seeat.data.repository


import com.pob.seeat.data.model.toUserInfoModel
import com.pob.seeat.data.remote.UserInfoSource
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.model.toUserInfo
import com.pob.seeat.domain.repository.UserInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(private val source : UserInfoSource) : UserInfoRepository{

    override suspend fun createUserInfo(userInfo: UserInfoModel) {
        source.createUserInfo(userInfo.toUserInfo())
    }

    override suspend fun getUserInfo(uid: String): Flow<UserInfoModel?> {
        return source.getUserInfo(uid).map { it?.toUserInfoModel() }
    }

    override suspend fun deleteUserInfo(userInfo: UserInfoModel) {
        source.deleteUserInfo(userInfo.toUserInfo())
    }

    override suspend fun updateUserInfo(userInfo: UserInfoModel) {
        source.updateUserInfo(userInfo.toUserInfo())
    }
}