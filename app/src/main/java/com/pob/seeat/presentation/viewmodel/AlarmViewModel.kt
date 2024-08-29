package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.UiState
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.domain.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {
    var alarmResponse = MutableStateFlow<UiState<List<AlarmModel>>>(UiState.Loading)
        private set

    fun getAlarmList() {
        viewModelScope.launch {
            alarmRepository
                .getAlarmList()
                .collect { alarmResponse.value = it }
        }
    }

    fun readAlarm(uId: String) {
        viewModelScope.launch {
            alarmRepository
                .readAlarm(uId)
                .collect { alarmResponse.value = it }
        }
    }
}