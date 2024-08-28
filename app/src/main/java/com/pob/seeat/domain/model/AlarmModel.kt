package com.pob.seeat.domain.model

import com.pob.seeat.data.model.AlarmResponse
import java.time.LocalDateTime

data class AlarmModel(
    val uId: String, // 알림 고유 ID
    val postId: String, // 게시물 고유 ID
    val postTitle: String, // 게시물 제목
    val postImage: String, // 게시물 대표 썸네일
    val content: String, // 알림 내용
    val createdAt: LocalDateTime, // 알림이 생성된 시간
    val isRead: Boolean = false, // 사용자가 확인한 알림인지 여부
)

fun List<AlarmResponse>.toAlarmModelList() = this.map { item ->
    AlarmModel(
        uId = item.uId,
        postId = item.postId ?: "",
        postTitle = item.postTitle ?: "",
        postImage = item.postImage ?: "",
        content = item.content ?: "",
        createdAt = item.createdAt ?: LocalDateTime.now(),
        isRead = item.isRead ?: false,
    )
}
