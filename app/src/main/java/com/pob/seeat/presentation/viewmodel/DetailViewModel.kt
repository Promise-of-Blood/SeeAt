package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.repository.FeedRepository
import com.pob.seeat.domain.usecase.UserInfoUseCases
import com.pob.seeat.utils.GoogleAuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val userInfoUseCases: UserInfoUseCases
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoModel?>(null)
    val userInfo: StateFlow<UserInfoModel?> get() = _userInfo

    private val _isLiked = MutableStateFlow<Boolean>(false)
    val isLiked: MutableStateFlow<Boolean> get() = _isLiked

    private val _singleFeedResponse = MutableStateFlow<Result<FeedModel>>(Result.Loading)
    val singleFeedResponse: StateFlow<Result<FeedModel>> get() = _singleFeedResponse

    val uid = GoogleAuthUtil.getUserUid()

    fun getFeedById(feedId: String) {
        viewModelScope.launch {
            feedRepository.getFeed(feedId).collect { uiState ->
                _singleFeedResponse.value = uiState
            }
        }
    }

    fun getUserUid(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid
        return userUid
    }

    fun getUserInfo(uid: String) {
        viewModelScope.launch {
            val result = userInfoUseCases.getUserInfoUseCase.execute(uid)
            _userInfo.value = result

        }
        Timber.i(userInfo.toString())
    }

    fun setIsLiked(isLiked: Boolean) {
        viewModelScope.launch {
            _isLiked.value = isLiked
        }
    }

    fun isLikedToggle(feedUid: String) {
        viewModelScope.launch {
            // Todo true면 숫자 -1하고 likedFeed에서 해당 게시글 id 삭제
            // Todo false면 숫자 +1하고 likedFedd에 해당 게시글 id 문서 추가
            when (isLiked.value) {
                true -> {
                    if (uid != null) {
                        userInfoUseCases.createLikedFeed.execute(uid, feedUid)
                    } else {
                        Timber.e("uid가 null입니다.")
                    }
                }

                false -> {}
            }
            _isLiked.value = !isLiked.value
        }
    }
}