package com.pob.seeat.data.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.remote.AlarmRemote
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(private val alarmRemoteDataSource: AlarmRemote) :
    AlarmRepository {

    override suspend fun getAlarmList(uId: String): Flow<Result<List<AlarmModel>>> = flow {
        emitAll(alarmRemoteDataSource.getAlarmList(uId))
    }

    override suspend fun readAlarm(uId: String, alarmId: String) {
        alarmRemoteDataSource.readAlarm(uId, alarmId)
    }

    override suspend fun deleteAlarm(uId: String, alarmId: String) {
        alarmRemoteDataSource.deleteAlarm(uId, alarmId)
    }
}