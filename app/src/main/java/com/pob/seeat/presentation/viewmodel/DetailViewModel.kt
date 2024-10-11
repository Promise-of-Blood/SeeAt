package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.pob.seeat.data.model.BookmarkEntity
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.model.FeedReportModel
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.repository.FeedRepository
import com.pob.seeat.domain.usecase.DeleteBookmarkUseCase
import com.pob.seeat.domain.usecase.EditFeedUseCase
import com.pob.seeat.domain.usecase.DeleteReportedFeedUseCase
import com.pob.seeat.domain.usecase.IsBookmarkedUseCase
import com.pob.seeat.domain.usecase.RemoveFeedUseCase
import com.pob.seeat.domain.usecase.ReportFeedUseCase
import com.pob.seeat.domain.usecase.SaveBookmarkUseCase
import com.pob.seeat.domain.usecase.UserInfoUseCases
import com.pob.seeat.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val userInfoUseCases: UserInfoUseCases,
    private val saveBookmarkUseCase: SaveBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val isBookmarkedUseCase: IsBookmarkedUseCase,
    private val reportFeedUseCase: ReportFeedUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val removeFeedUseCase: RemoveFeedUseCase,
    private val editFeedUseCase: EditFeedUseCase,
    private val deleteReportedFeedUseCase: DeleteReportedFeedUseCase,
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoModel?>(null)
    val userInfo: StateFlow<UserInfoModel?> get() = _userInfo

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _singleFeedResponse = MutableStateFlow<Result<FeedModel>>(Result.Loading)
    val singleFeedResponse: StateFlow<Result<FeedModel>> get() = _singleFeedResponse

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> get() = _isBookmarked

    val uid: String? = firebaseAuth.currentUser?.uid

    fun modifyIsLiked(count: Int) {
        viewModelScope.launch {
            if (isLiked.value) {
                EventBus.post(count + 1)
            } else {
                EventBus.post(count - 1)
            }

        }
    }

    fun getFeedById(feedId: String, userLocation: GeoPoint) {
        viewModelScope.launch {
            feedRepository.getFeed(feedId, userLocation).collect { uiState ->
                _singleFeedResponse.value = uiState
            }
        }
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
            when (isLiked.value) {
                true -> {
                    if (uid != null) {
                        _isLiked.value = false
                        userInfoUseCases.removeLikedFeed.execute(uid, feedUid)
                        feedRepository.setLikeMinus(feedUid)
                    } else {
                        Timber.e("uid가 null입니다.")
                    }
                }

                false -> {
                    if (uid != null) {
                        _isLiked.value = true
                        userInfoUseCases.createLikedFeed.execute(uid, feedUid)
                        feedRepository.setLikePlus(feedUid)
                    } else {
                        Timber.e("uid가 null입니다.")
                    }
                }
            }

        }
    }

    fun saveBookmark(feed: BookmarkEntity) {
        viewModelScope.launch {
            saveBookmarkUseCase(feed)
            getIsBookmarked(feed.feedId)
        }
    }

    fun deleteBookmark(feedId: String) {
        viewModelScope.launch {
            deleteBookmarkUseCase(feedId)
            getIsBookmarked(feedId)
        }
    }

    fun getIsBookmarked(feedId: String) {
        viewModelScope.launch {
            _isBookmarked.value = isBookmarkedUseCase(feedId)
        }
    }

    fun addReportFeed(feedReportModel: FeedReportModel) {
        viewModelScope.launch {
            reportFeedUseCase(feedReportModel)
        }
    }

    fun removeFeed(feedId: String) {
        viewModelScope.launch {
            removeFeedUseCase(feedId)
        }
    }

    fun editFeed(feedModel: FeedModel) {
        viewModelScope.launch {

            try {
                Timber.tag("editFeedRemote").i("feedMap: $feedModel")
                editFeedUseCase(feedModel)
            } catch (e: Exception) {
                Timber.e(e, "Error occurred in editFeed")
            }

        }
    }


    fun deleteReportedFeed(feedId: String) {
        viewModelScope.launch {
            deleteReportedFeedUseCase(feedId)
        }
    }
}