package com.pob.seeat.presentation.viewmodel

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.pob.seeat.domain.model.TempUserInfo
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.usecase.UserInfoUseCases
import com.pob.seeat.utils.GoogleAuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val userInfoUseCases: UserInfoUseCases) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoModel?>(null)
    val userInfo : StateFlow<UserInfoModel?> get() = _userInfo

    private val _tempUserInfo = MutableStateFlow<TempUserInfo?>(null)
    val tempUserInfo: StateFlow<TempUserInfo?> get() = _tempUserInfo

    private val _profileUploadResult = MutableStateFlow<String?>(null)
    val profileUploadResult : StateFlow<String?> = _profileUploadResult

    private val storage = FirebaseStorage.getInstance().reference

    fun signUp(uid:String, email:String, nickname:String, profileUrl : String, introduce: String,token: String){
        val newUser = UserInfoModel(uid,email,nickname,profileUrl,introduce,token)

        viewModelScope.launch {
            userInfoUseCases.createUserInfoUseCase.execute(newUser)
        }
    }

    fun uploadProfileImage(imageUri: Uri, uid: String) {
        viewModelScope.launch {
            flow {
                emit(withContext(Dispatchers.IO) {
                    val file = storage.child("profile_images/$uid.jpg")
                    file.putFile(imageUri).await()
                    file.downloadUrl.await().toString()
                })
            }
                .onStart {
                    // 업로드 시작할 때 필요한 로직 추가
                    Log.d("ImageUpload", "이미지 업로드 시작")
                    _profileUploadResult.value = "LOADING"
                }
                .catch { e ->
                    Log.e("ImageUpload", "이미지 업로드 실패 ${e.message}")
                    emit("") // 실패 시 빈 문자열 반환
                }
                .collect { downloadUrl ->
                    _profileUploadResult.value = downloadUrl
                    Log.d("이미지 url","$downloadUrl")
                }
        }
    }


    fun editProfile(uid: String, nickname: String, introduce: String, profileUrl: String) {
        viewModelScope.launch {
            val currentUserInfo = userInfoUseCases.getUserInfoUseCase.execute(uid)
            if (currentUserInfo != null) {
                val updatedProfile = currentUserInfo.copy(
                    nickname = nickname,
                    introduce = introduce,
                    profileUrl = profileUrl
                )
                userInfoUseCases.updateUserInfoUseCase.execute(updatedProfile)
                _userInfo.value = updatedProfile
            } else {
                Log.e("editProfile", "유저정보를 찾을 수 없습니다.")
            }
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