package com.pob.seeat.data.repository

import com.pob.seeat.data.model.ChatListModel
import com.pob.seeat.data.model.ChatMemberModel
import com.pob.seeat.data.model.ChatModel
import com.pob.seeat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

// TODO 구조 변경 해야 됨 -> 최대한 SOLID 맞추게

class ChatRepositoryImpl : ChatRepository {
    override suspend fun getMyChatList(): Flow<Result<List<ChatListModel>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatPartner(feedId: String): Flow<Result<ChatMemberModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(targetUid: String, feedId: String, message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun receiveMessage(feedId: String): Flow<Result<ChatModel>> {
        TODO("Not yet implemented")
    }
}