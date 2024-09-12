package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.domain.usecase.AlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmUseCase: AlarmUseCase
) : ViewModel() {
    var alarmResponse = MutableStateFlow<Result<List<AlarmModel>>>(Result.Loading)
        private set

    fun getAlarmList() {
        viewModelScope.launch {
            alarmUseCase()
                .collect {
                    when (it) {
                        is Result.Loading -> alarmResponse.value = Result.Loading
                        is Result.Error -> alarmResponse.value = Result.Error(it.message)
                        is Result.Success -> alarmResponse.value = Result.Success(it.data)
                    }
                }
        }
    }

    fun readAlarm(alarmId: String) {
        viewModelScope.launch { alarmUseCase.readAlarm(alarmId) }
    }

    fun deleteAlarm(alarmId: String) {
        viewModelScope.launch { alarmUseCase.deleteAlarm(alarmId) }
    }
}
