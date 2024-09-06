package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.FeedRepository
import com.pob.seeat.domain.usecase.AlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val alarmUseCase: AlarmUseCase,
) : ViewModel() {
    private val _feedResponse = MutableStateFlow<Result<List<FeedModel>>>(Result.Loading)
    val feedResponse: StateFlow<Result<List<FeedModel>>> = _feedResponse

    private val _singleFeedResponse = MutableStateFlow<Result<FeedModel>>(Result.Loading)
    val singleFeedResponse: StateFlow<Result<FeedModel>> = _singleFeedResponse

    private val _unreadAlarmCount = MutableStateFlow<Result<Long>>(Result.Loading)
    val unreadAlarmCount: StateFlow<Result<Long>> = _unreadAlarmCount

    fun getFeedList() {
        viewModelScope.launch {
            feedRepository.getFeedList().collect { uiState ->
                _feedResponse.value = uiState
            }
        }
    }

    fun getFeedById(feedId: String) {
        viewModelScope.launch {
            feedRepository.getFeed(feedId).collect { uiState ->
                _singleFeedResponse.value = uiState
            }
        }
    }

    fun getUnReadAlarmCount() {
        viewModelScope.launch {
            alarmUseCase.getUnreadAlarmCount().collect { uiState ->
                _unreadAlarmCount.value = uiState
            }
        }
    }
}