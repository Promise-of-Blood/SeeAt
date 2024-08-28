package com.pob.seeat.data.repository

import com.pob.seeat.data.mockup.AlarmCacheDataSource
import com.pob.seeat.data.model.UiState
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.domain.model.toAlarmModelList
import com.pob.seeat.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(private val alarmRemoteDataSource: AlarmCacheDataSource) :
    AlarmRepository {

    override suspend fun getAlarmList(): Flow<UiState<List<AlarmModel>>> {
        return flow {
            val alarmList = alarmRemoteDataSource.getAlarmList()
            val alarmResponse =
                if (alarmList.isNotEmpty()) UiState.Success(alarmList.toAlarmModelList())
                else UiState.Error("알림 목록을 가져오는데 실패했습니다.")
            emit(alarmResponse)
        }
    }

    override suspend fun readAlarm(uId: String): Flow<UiState<List<AlarmModel>>> {
        return flow {
            val alarmList = alarmRemoteDataSource.getAlarmList().map {
                if (it.uId == uId) it.copy(isRead = true) else it
            }
            val alarmResponse =
                if (alarmList.isNotEmpty()) {
                    alarmRemoteDataSource.setAlarmList(alarmList)
                    UiState.Success(alarmList.toAlarmModelList())
                }
                else UiState.Error("요청을 처리하는데 실패했습니다.")
            emit(alarmResponse)
        }
    }
}