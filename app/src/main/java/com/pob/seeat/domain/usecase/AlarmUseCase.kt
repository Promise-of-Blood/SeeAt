package com.pob.seeat.domain.usecase

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.domain.repository.AlarmRepository
import com.pob.seeat.domain.repository.UserInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AlarmUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val alarmRepository: AlarmRepository,
) {
    suspend operator fun invoke(): Flow<Result<List<AlarmModel>>> {
        val currentUserId = userInfoRepository.getCurrentUserUid().firstOrNull() ?: ""
        return alarmRepository.getAlarmList(currentUserId)
    }

    suspend fun readAlarm(alarmId: String) {
        val currentUserId = userInfoRepository.getCurrentUserUid().firstOrNull() ?: ""
        alarmRepository.readAlarm(currentUserId, alarmId)
    }

    suspend fun deleteAlarm(alarmId: String) {
        val currentUserId = userInfoRepository.getCurrentUserUid().firstOrNull() ?: ""
        alarmRepository.deleteAlarm(currentUserId, alarmId)
    }
}