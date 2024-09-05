package com.pob.seeat.data.repository

import com.pob.seeat.data.model.ChatListModel
import com.pob.seeat.data.model.ChatMemberModel
import com.pob.seeat.data.model.ChatModel
import com.pob.seeat.data.remote.ChatRemote
import com.pob.seeat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.pob.seeat.data.model.Result

// TODO 구조 변경 해야 됨 -> 최대한 클린 아키텍처, SOLID 맞추게

class ChatRepositoryImpl @Inject constructor(private val chatRemote: ChatRemote) : ChatRepository {
    override suspend fun getMyChatList(): Flow<Result<List<ChatListModel>>> {
        return flow { emit( chatRemote.getMyChatList() ) }
    }

    override suspend fun getChatPartner(feedId: String): Flow<Result<ChatMemberModel>> {
        return flow { emit( chatRemote.getChatPartner(feedId) ) }
    }

    override suspend fun sendMessage(targetUid: String, feedId: String, message: String) {
        return chatRemote.sendMessage(targetUid, feedId, message)
    }

    override suspend fun receiveMessage(feedId: String): Flow<Result<ChatModel>> {
        return flow { emit( chatRemote.receiveMessage(feedId) ) }
    }

    override suspend fun initMessage(feedId: String): Flow<List<Result<ChatModel>>> {
        return flow { emit( chatRemote.initMessage(feedId) ) }
    }
}