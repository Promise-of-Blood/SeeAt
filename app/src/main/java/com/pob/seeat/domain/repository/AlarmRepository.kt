package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.UiState
import com.pob.seeat.domain.model.AlarmModel
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun getAlarmList(): Flow<UiState<List<AlarmModel>>>
    suspend fun readAlarm(uId: String): Flow<UiState<List<AlarmModel>>>
}