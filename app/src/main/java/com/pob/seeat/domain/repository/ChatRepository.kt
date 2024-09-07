package com.pob.seeat.domain.repository

import kotlinx.coroutines.flow.Flow
import com.pob.seeat.data.model.Result
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem

interface ChatRepository {
    suspend fun getMyChatList(): Flow<Result<List<ChatListModel>>>
    suspend fun getChatPartner(feedId: String): Flow<Result<ChatMemberModel>>
    suspend fun sendMessage(targetUid: String, feedId: String, message: String)
    fun receiveMessage(feedId: String): Flow<Result<ChattingUiItem>>
    suspend fun initMessage(feedId: String): Flow<List<Result<ChattingUiItem>>>
}