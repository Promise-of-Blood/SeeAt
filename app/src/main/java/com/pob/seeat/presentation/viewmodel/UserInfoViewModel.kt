package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.usecase.UserInfoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val userInfoUseCases: UserInfoUseCases) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoModel?>(null)
    val userInfo : StateFlow<UserInfoModel?> get() = _userInfo

    fun signUp(uid:String, email:String, nickname:String){
        val newUser = UserInfoModel(uid,email,nickname)

        viewModelScope.launch {
            userInfoUseCases.createUserInfoUseCase.execute(newUser)
        }
    }

    fun createUserInfo(userInfo : UserInfoModel){
        viewModelScope.launch {
            userInfoUseCases.createUserInfoUseCase.execute(userInfo)
        }
    }

    fun updateUserInfo(userInfo: UserInfoModel){
        viewModelScope.launch {
            userInfoUseCases.updateUserInfoUseCase.execute(userInfo)
        }
    }

    fun deleteUserInfo(userInfo: UserInfoModel){
        viewModelScope.launch {
            userInfoUseCases.deleteUserInfoUseCase.execute(userInfo)
        }
    }

    fun getUserInfo(uid:String){
        viewModelScope.launch {
            val result = userInfoUseCases.getUserInfoUseCase.execute(uid)
            _userInfo.value = result
        }
    }
}