package com.pob.seeat.domain.usecase


import com.google.firebase.auth.UserInfo
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.repository.UserInfoRepository
import kotlinx.coroutines.flow.firstOrNull

data class UserInfoUseCases(
    val createUserInfoUseCase : CreateUserInfoUseCase,
    val updateUserInfoUseCase : UpdateUserInfoUseCase,
    val deleteUserInfoUseCase : DeleteUserInfoUseCase,
    val getUserInfoUseCase : GetUserInfoUseCase,
    val getUserInfoByEmailUseCase : GetUserInfoByEmailUseCase
)

class CreateUserInfoUseCase(private val repository: UserInfoRepository){
    suspend fun execute(userInfo: UserInfoModel){
        repository.createUserInfo(userInfo)
    }
}

class UpdateUserInfoUseCase(private val repository: UserInfoRepository){
    suspend fun execute(userInfo: UserInfoModel){
        repository.updateUserInfo(userInfo)
    }
}

class DeleteUserInfoUseCase(private val repository: UserInfoRepository){
    suspend fun execute(userInfo: UserInfoModel){
        repository.deleteUserInfo(userInfo)
    }
}

class GetUserInfoUseCase(private val repository: UserInfoRepository){
    suspend fun execute(uid:String):UserInfoModel?{
        return repository.getUserInfo(uid).firstOrNull()
    }
}

class GetUserInfoByEmailUseCase(private val repository : UserInfoRepository){
    suspend fun execute(email:String):UserInfoModel?{
        return repository.getUserInfoByEmail(email).firstOrNull()
    }
}