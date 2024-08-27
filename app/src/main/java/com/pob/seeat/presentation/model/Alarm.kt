package com.pob.seeat.presentation.model

data class Alarm(
    val id: String,
    val title: String,
    val content: String,
    val time: String, // 알림이 생성된 시간
    val type: String = "comment", // 새로운 댓글에 대한 알림인지, 좋아요에 대한 알림인지
    val isRead: Boolean = false, // 사용자가 확인한 알림인지 여부
    val image: String = "https://picsum.photos/200", // 게시물의 썸네일
    val postId: String = "somerandomid", // 게시물의 식별 가능한 id 값
)
