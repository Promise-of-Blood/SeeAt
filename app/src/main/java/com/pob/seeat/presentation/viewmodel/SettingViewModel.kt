package com.pob.seeat.presentation.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pob.seeat.data.remote.UserInfoSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userInfoSourceImpl: UserInfoSource
) : ViewModel() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun switchChatNotiOn(isChecked: Boolean) {
        userInfoSourceImpl.switchChatNotiOn(uid ?: "", isChecked)
    }

    suspend fun switchCommentNotiOn(isChecked: Boolean) {
        userInfoSourceImpl.switchCommentNotiOn(uid ?: "", isChecked)
    }

    suspend fun getChatNotiOn(): Boolean {
        return userInfoSourceImpl.getChatNotiOn(uid ?: "")
    }

    suspend fun getCommentNotiOn(): Boolean {
        return userInfoSourceImpl.getCommentNotiOn(uid ?: "")
    }

}