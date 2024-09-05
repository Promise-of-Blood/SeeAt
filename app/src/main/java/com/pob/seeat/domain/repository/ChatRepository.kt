package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.ChatListModel
import com.pob.seeat.data.model.ChatMemberModel
import com.pob.seeat.data.model.ChatModel
import kotlinx.coroutines.flow.Flow
import com.pob.seeat.data.model.Result

interface ChatRepository {
    suspend fun getMyChatList(): Flow<Result<List<ChatListModel>>>
    suspend fun getChatPartner(feedId: String): Flow<Result<ChatMemberModel>>
    suspend fun sendMessage(targetUid: String, feedId: String, message: String)
    suspend fun receiveMessage(feedId: String): Flow<Result<ChatModel>>
    suspend fun initMessage(feedId: String): Flow<List<Result<ChatModel>>>
}