package com.pob.seeat.domain.repository

import kotlinx.coroutines.flow.Flow
import com.pob.seeat.data.model.Result
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem

interface ChatListRepository {
    fun receiveChatList(): Flow<Result<ChatListUiItem>>
}