package com.pob.seeat.data.model

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.util.UUID

data class AlarmResponse(
    val alarmId: String? = "", // 알림 고유 ID
    val feedId: String? = "", // 게시물 고유 ID
    val feedTitle: String? = "", // 게시물 제목
    val feedImage: String? = "", // 게시물 대표 썸네일
    val comment: String? = "", // 알림 내용
    val timestamp: Timestamp? = null, // 댓글이 생성된 시간
    val isRead: Boolean? = false, // 사용자가 확인한 알림인지 여부
)
