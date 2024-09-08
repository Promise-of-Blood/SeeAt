package com.pob.seeat.presentation.view.chat.items

import java.util.Date
import java.util.UUID

data class ChatListUiItem(
    val personId: String,
    val person: String,
    val icon: String,
    val content: String,
    // TODO 테스트 중엔 String, 나중에 타입 Date로 바꿔서 로직으로 계산
    val lastTime: String,
    val unreadMessageCount: Int,
)