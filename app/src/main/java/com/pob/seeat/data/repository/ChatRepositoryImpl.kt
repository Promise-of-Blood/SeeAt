package com.pob.seeat.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.pob.seeat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.chat.ChatModel
import com.pob.seeat.data.model.chat.ChatsChattingModel
import com.pob.seeat.data.model.chat.MessageModel
import com.pob.seeat.data.model.chat.MessagesInfoModel
import com.pob.seeat.data.remote.chat.ChatsRemote
import com.pob.seeat.data.remote.chat.MessagesRemote
import com.pob.seeat.data.remote.chat.UsersRemote
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.utils.GoogleAuthUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber

// TODO 구조 변경 해야 됨 -> 최대한 클린 아키텍처, SOLID 맞추게

class ChatRepositoryImpl @Inject constructor(
    private val chatsRemote: ChatsRemote,
    private val messagesRemote: MessagesRemote,
    private val usersRemote: UsersRemote,
) : ChatRepository {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override suspend fun sendMessage(feedId: String, targetUid: String, message: String) {
        var chatId = usersRemote.getChatId(userId = uid, feedId = feedId)
        if(chatId == "none") {
            chatId = chatsRemote.createChat(ChatsChattingModel(
                feedFrom = feedId,
                lastMessage = message,
                users = mapOf(uid to true, targetUid to true),
                whenLast = Timestamp.now(),
            ))
            usersRemote.createUserChat(feedId, chatId)
        } else {
            chatsRemote.saveChat(ChatsChattingModel(
                feedFrom = feedId,
                lastMessage = message,
                users = mapOf(uid to true, targetUid to true),
                whenLast = Timestamp.now(),
            ), chatId)
        }
        messagesRemote.sendMessage(
            chatId = chatId,
            targetUid = targetUid,
            message = message,
        )
    }

    override fun receiveMessage(feedId: String): Flow<Result<ChattingUiItem>> {
        val chatId = usersRemote.getChatId(userId = uid, feedId = feedId)
        val remoteMessage = messagesRemote.receiveMessage(chatId)

        return remoteMessage.map {
            when(it) {
                is Result.Success -> Result.Success(it.data.toChattingUiItem())
                is Result.Error -> Result.Error(it.message)
                Result.Loading -> Result.Loading
            }
        }
    }

    override suspend fun initMessage(feedId: String): List<Result<ChattingUiItem>> {
        return when (val messages = messagesRemote.initMessage(feedId)) {
            is Result.Success -> {
                messages.data.map {
                    Result.Success(it.toChattingUiItem())
                }
            }
            is Result.Error -> {
                listOf(Result.Error(messages.message))
            }
            Result.Loading -> {
                listOf(Result.Loading)
            }
        }
    }
}

fun MessagesInfoModel.toChattingUiItem(): ChattingUiItem {
    val uid = GoogleAuthUtil.getUserUid()
    return if (this.sender == uid) {
        ChattingUiItem.MyChatItem(
            message = this.message,
            time = this.timestamp.toString()
        )
    } else {
        ChattingUiItem.YourChatItem(
            message = this.message,
            time = this.timestamp.toString()
        )
    }
}