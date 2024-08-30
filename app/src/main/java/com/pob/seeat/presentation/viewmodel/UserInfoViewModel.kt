package com.pob.seeat.presentation.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.domain.model.TempUserInfo
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.usecase.UserInfoUseCases
import com.pob.seeat.utils.GoogleAuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val userInfoUseCases: UserInfoUseCases) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoModel?>(null)
    val userInfo : StateFlow<UserInfoModel?> get() = _userInfo

    private val _tempUserInfo = MutableStateFlow<TempUserInfo?>(null)
    val tempUserInfo: StateFlow<TempUserInfo?> get() = _tempUserInfo

    fun signUp(uid:String, email:String, nickname:String, profileUrl : String, introduce: String){
        val newUser = UserInfoModel(uid,email,nickname,profileUrl,introduce)

        viewModelScope.launch {
            userInfoUseCases.createUserInfoUseCase.execute(newUser)
        }
    }


    fun saveTempUserInfo(uid:String? = null,email: String? = null, name:String? = null, profileUrl: String?= null, introduce: String?= null){
        val currentInfo = _tempUserInfo.value

        val updatedInfo = TempUserInfo(
            uid = uid ?: currentInfo?.uid ?: "",
            email = email ?: currentInfo?.email ?: "",
            name = name ?: currentInfo?.name ?: "",
            profileUrl = profileUrl ?:currentInfo?.profileUrl ?: "",
            introduce = introduce ?:currentInfo?.introduce ?: ""
        )

        _tempUserInfo.value = updatedInfo
    }



    fun isOurFamily(email: String, onUserExists : () -> Unit, onUserNotExist : () -> Unit){
        viewModelScope.launch {
            try {
                val user = userInfoUseCases.getUserInfoByEmailUseCase.execute(email)
                if(user != null){
                    _userInfo.value = user
                    onUserExists()
                }else{
                    onUserNotExist()
                }
            }catch (e: Exception){
                Log.e("회원확인","오류 $e")
            }
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