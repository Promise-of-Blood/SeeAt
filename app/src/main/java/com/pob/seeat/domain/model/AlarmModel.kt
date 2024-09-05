package com.pob.seeat.domain.model

import com.pob.seeat.data.model.AlarmResponse
import com.pob.seeat.utils.Utils.toLocalDateTime
import java.time.LocalDateTime

data class AlarmModel(
    val alarmId: String = "", // 알림 고유 ID
    val feedId: String = "", // 게시물 고유 ID
    val feedTitle: String = "", // 게시물 제목
    val feedImage: String = "", // 게시물 대표 썸네일
    val content: String = "", // 알림 내용
    val createdAt: LocalDateTime? = null, // 댓글이 생성된 시간
    val isRead: Boolean = false, // 사용자가 확인한 알림인지 여부
)

fun List<AlarmResponse>.toAlarmModelList() = this.map(AlarmResponse::toAlarmModel)

fun AlarmResponse.toAlarmModel() = AlarmModel(
    alarmId = this.alarmId ?: "",
    feedId = this.feedId ?: "",
    feedTitle = this.feedTitle ?: "",
    feedImage = this.feedImage ?: "",
    content = this.comment ?: "",
    createdAt = this.timestamp?.toLocalDateTime(),
    isRead = this.isRead ?: false,
)
