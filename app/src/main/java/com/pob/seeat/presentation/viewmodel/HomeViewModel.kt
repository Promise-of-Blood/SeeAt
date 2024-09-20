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

    private val _unreadAlarmCount = MutableStateFlow<Result<Long>>(Result.Loading)
    val unreadAlarmCount: StateFlow<Result<Long>> = _unreadAlarmCount

    var screenWidth: Int? = null
    var screenHeight: Int? = null

    fun getFeedList(
        centerLat: Double,
        centerLng: Double,
        radiusInKm: Double
    ) {
        viewModelScope.launch {
            feedRepository.getFeedList(centerLat, centerLng, radiusInKm).collect { uiState ->
                _feedResponse.value = uiState
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