package com.pob.seeat.presentation.view.chat.items


data class ChatListUiItem(
    val id: String,
    val person: String,
    val icon: String,
    val content: String,
    // TODO 테스트 중엔 String, 나중에 타입 Date로 바꿔서 로직으로 계산
    val lastTime: Long,
    val unreadMessageCount: Int,
    val feedFrom: String,
)