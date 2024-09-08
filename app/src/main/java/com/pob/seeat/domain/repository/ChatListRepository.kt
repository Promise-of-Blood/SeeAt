package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.chat.ChatsChattingModel
import kotlinx.coroutines.flow.Flow
import com.pob.seeat.data.model.Result

interface ChatListRepository {
    fun receiveChatList(): Flow<Result<ChatsChattingModel>>
}