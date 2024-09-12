package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.AlarmModel
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun getAlarmList(uId: String): Flow<Result<List<AlarmModel>>>
    suspend fun readAlarm(uId: String, alarmId: String)
    suspend fun deleteAlarm(uId: String, alarmId: String)
    suspend fun getUnreadAlarmCount(uId: String): Flow<Result<Long>>
}