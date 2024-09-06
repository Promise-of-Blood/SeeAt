package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.usecase.AlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val alarmUseCase: AlarmUseCase,
) : ViewModel() {
    private val _unreadAlarmCount = MutableStateFlow<Result<Long>>(Result.Loading)
    val unreadAlarmCount: StateFlow<Result<Long>> = _unreadAlarmCount

    fun getUnReadAlarmCount() {
        viewModelScope.launch {
            alarmUseCase.getUnreadAlarmCount().collect {
                _unreadAlarmCount.value = it
            }
        }
    }
}