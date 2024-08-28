package com.pob.seeat.data.model

import java.time.LocalDateTime
import java.util.UUID

data class AlarmResponse(
    val uId: String = UUID.randomUUID().toString(), // 알림 고유 ID
    val postId: String?, // 게시물 고유 ID
    val postTitle: String?, // 게시물 제목
    val postImage: String?, // 게시물 대표 썸네일
    val content: String?, // 알림 내용
    val createdAt: LocalDateTime?, // 알림이 생성된 시간
    val isRead: Boolean?, // 사용자가 확인한 알림인지 여부
)