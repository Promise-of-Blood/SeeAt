package com.pob.seeat.presentation.view.chat.items

import java.time.LocalDateTime
import java.util.UUID

sealed class ChattingUiItem {
    abstract val id: String
    data class MyChatItem(
        override val id: String,
        val message: String,
        val time: LocalDateTime,
        val isShowTime : Boolean = true,
    ) : ChattingUiItem()
    data class YourChatItem(
        override val id: String,
        val message: String,
        val time: LocalDateTime,
        val isShowTime : Boolean = true,
    ) : ChattingUiItem()
//    TODO 추후에 id 값에 타임스탬프를 넣거나 하는 식으로 하는 게 좋을 듯,
    data class OnlyTimeItem(
        override val id: String,
        val time: LocalDateTime,
    ) : ChattingUiItem()
}