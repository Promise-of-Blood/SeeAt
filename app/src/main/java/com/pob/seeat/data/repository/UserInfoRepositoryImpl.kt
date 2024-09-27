package com.pob.seeat.data.repository


import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.toUserInfoModel
import com.pob.seeat.data.remote.UserInfoSource
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.model.toUserInfo
import com.pob.seeat.domain.repository.UserInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(private val source: UserInfoSource) :
    UserInfoRepository {

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

    override suspend fun getCurrentUserUid(): Flow<String> {
        return source.getCurrentUserUid()
    }

    override suspend fun getUserInfoByEmail(email: String): Flow<UserInfoModel?> {
        return source.getUserInfoByEmail(email).map { it?.toUserInfoModel() }
    }

    override suspend fun createLikedFeed(uid: String, feedUid: String) {
        return source.createLikedFeed(uid, feedUid)
    }

    override suspend fun removeLikedFeed(uid: String, feedUid: String) {
        return source.removeLikedFeed(uid, feedUid)
    }

    override suspend fun getUserList(): Flow<Result<List<UserInfoModel>>> = flow {
        emit(Result.Loading)
        try {
            val data = source.getUserList().map { it.toUserInfoModel() }
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun updateIsAdmin(uid: String, isAdmin: Boolean) {
        source.updateIsAdmin(uid, isAdmin)
    }

    override suspend fun deleteAllUserInfo(uid: String): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            source.deleteAllUserInfo(uid)
            emit(Result.Success("계정 정보를 성공적으로 삭제했습니다."))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "계정을 삭제하지 못했습니다."))
        }
    }
}