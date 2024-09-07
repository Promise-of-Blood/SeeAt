package com.pob.seeat.data.repository

import com.pob.seeat.data.remote.ChatRemote
import com.pob.seeat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.utils.GoogleAuthUtil
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber

// TODO 구조 변경 해야 됨 -> 최대한 클린 아키텍처, SOLID 맞추게

class ChatRepositoryImpl @Inject constructor(private val chatRemote: ChatRemote) : ChatRepository {
    override suspend fun getMyChatList(): Flow<Result<List<ChatListModel>>> {
        return flow { emit(chatRemote.getMyChatList()) }
    }

    override suspend fun getChatPartner(feedId: String): Flow<Result<ChatMemberModel>> {
        return flow { emit(chatRemote.getChatPartner(feedId)) }
    }

    override suspend fun sendMessage(targetUid: String, feedId: String, message: String) {
        return chatRemote.sendMessage(targetUid, feedId, message)
    }

    override fun receiveMessage(feedId: String): Flow<Result<ChattingUiItem>> {
        return chatRemote.receiveMessage(feedId).map {
            Timber.d("중복 확인")
            it.toChattingUiItem()
        }
    }

    override suspend fun initMessage(feedId: String): Flow<List<Result<ChattingUiItem>>> {
        return flow {
            emit(chatRemote.initMessage(feedId).map {
                it.toChattingUiItem()
            })
        }
    }
}

fun Result<ChatModel>.toChattingUiItem(): Result<ChattingUiItem> {
    val uid = GoogleAuthUtil.getUserUid()
    return if (this is Result.Success) {
        if (this.data.sender == uid) {
            Result.Success(
                ChattingUiItem.MyChatItem(
                    message = this.data.message.toString(),
                    time = this.data.timestamp.toString()
                )
            )
        } else {
            Result.Success(
                ChattingUiItem.YourChatItem(
                    message = this.data.message.toString(),
                    time = this.data.timestamp.toString()
                )
            )
        }
    } else if (this is Result.Error) Result.Error(this.message)
    else Result.Loading
}