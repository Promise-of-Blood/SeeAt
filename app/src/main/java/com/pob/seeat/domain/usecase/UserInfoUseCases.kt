package com.pob.seeat.domain.usecase


import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.repository.UserInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

data class UserInfoUseCases(
    val createUserInfoUseCase: CreateUserInfoUseCase,
    val updateUserInfoUseCase: UpdateUserInfoUseCase,
    val deleteUserInfoUseCase: DeleteUserInfoUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase,
    val getUserInfoByEmailUseCase: GetUserInfoByEmailUseCase,
    val createLikedFeed: CreateLikedFeed,
    val removeLikedFeed: RemoveLikedFeed,
)

class CreateUserInfoUseCase(private val repository: UserInfoRepository) {
    suspend fun execute(userInfo: UserInfoModel) {
        repository.createUserInfo(userInfo)
    }
}

class UpdateUserInfoUseCase(private val repository: UserInfoRepository) {
    suspend fun execute(userInfo: UserInfoModel) {
        repository.updateUserInfo(userInfo)
    }
}

class DeleteUserInfoUseCase(private val repository: UserInfoRepository) {
    suspend fun execute(userInfo: UserInfoModel) {
        repository.deleteUserInfo(userInfo)
    }
}

class GetUserInfoUseCase(private val repository: UserInfoRepository) {
    suspend fun execute(uid: String): UserInfoModel? {
        return repository.getUserInfo(uid).firstOrNull()
    }
}

class GetUserInfoByEmailUseCase(private val repository: UserInfoRepository) {
    suspend fun execute(email: String): UserInfoModel? {
        return repository.getUserInfoByEmail(email).firstOrNull()
    }
}

class CreateLikedFeed(private val repository: UserInfoRepository) {
    suspend fun execute(uid: String, feedUid: String) {
        repository.createLikedFeed(uid, feedUid)
    }
}

class RemoveLikedFeed(private val repository: UserInfoRepository) {
    suspend fun execute(uid: String, feedUid: String) {
        repository.removeLikedFeed(uid, feedUid)
    }
}

class GetUserListUseCase @Inject constructor(private val repository: UserInfoRepository) {
    suspend operator fun invoke() = repository.getUserList()
}

class UpdateIsAdminUseCase @Inject constructor(private val repository: UserInfoRepository) {
    suspend operator fun invoke(uid: String, isAdmin: Boolean) {
        repository.updateIsAdmin(uid, isAdmin)
    }
}

class DeleteAllUserInfoUseCase @Inject constructor(private val repository: UserInfoRepository) {
    suspend operator fun invoke(uid: String): Flow<Result<String>> {
        return repository.deleteAllUserInfo(uid)
    }
}