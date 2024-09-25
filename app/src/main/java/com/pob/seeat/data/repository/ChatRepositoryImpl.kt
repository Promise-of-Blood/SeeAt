package com.pob.seeat.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.pob.seeat.data.database.chat.ChatEntity
import com.pob.seeat.data.database.chat.ChatRoomDb
import com.pob.seeat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.chat.ChatsChattingModel
import com.pob.seeat.data.model.chat.MessagesInfoModel
import com.pob.seeat.data.remote.chat.ChatsRemote
import com.pob.seeat.data.remote.chat.MessagesRemote
import com.pob.seeat.data.remote.chat.UsersRemote
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.utils.Utils.toLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// TODO 구조 변경 해야 됨 -> 최대한 클린 아키텍처, SOLID 맞추게

class ChatRepositoryImpl @Inject constructor(
    private val chatsRemote: ChatsRemote,
    private val messagesRemote: MessagesRemote,
    private val usersRemote: UsersRemote,
) : ChatRepository {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//    var chatIdInClass = flow<String> {  }

    override suspend fun sendMessage(
        feedId: String,
        targetUid: String,
        message: String,
        chatId: String,
    ): Boolean {
        var mutableChatId = chatId
        if (chatId == "none") {
            Timber.tag("sendMessage's chatId!").d("chatId none!")
            mutableChatId = chatsRemote.createChat(
                ChatsChattingModel(
                    feedFrom = feedId,
                    lastMessage = message,
                    whenLast = System.currentTimeMillis(),
                    userList = listOf(uid, targetUid),
                    sender = uid,
                )
            )
            Timber.tag("sendMessage's base chatId").d("chatId $chatId")
            Timber.tag("sendMessage's new chatId").d("chatId $mutableChatId")
            usersRemote.createUserChat(feedId = feedId, chatId = mutableChatId, userId = uid)
            usersRemote.createUserChat(feedId = feedId, chatId = mutableChatId, userId = targetUid)
        } else {
            chatsRemote.saveChat(
                ChatsChattingModel(
                    feedFrom = feedId,
                    lastMessage = message,
                    whenLast = System.currentTimeMillis(),
                    userList = listOf(uid, targetUid),
                    sender = uid,
                ), chatId = mutableChatId
            )
        }
        return messagesRemote.sendMessage(
            chatId = mutableChatId,
            message = message,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun receiveMessage(
        feedId: String,
        chatId: String,
    ): Flow<Result<ChattingUiItem>> {
//        chatIdInClass = subscribeChatId(feedId)
//        chatIdInClass.collectLatest { Timber.tag("receiveMessage's chatIdInClass").d(it) }

        var chatIdFlow = flow { emit(chatId) }

        return chatIdFlow.flatMapLatest {
            messagesRemote.receiveMessage(it).map { message ->
                when (message) {
                    is Result.Success -> Result.Success(message.data.toChattingUiItem())
                    is Result.Error -> Result.Error(message.message)
                    Result.Loading -> Result.Loading
                }
            }
        }
    }

    override suspend fun receiveMessage(feedId: String): Flow<Result<ChattingUiItem>> {
        val chatIdFlow = subscribeChatId(feedId)
        chatIdFlow.collectLatest { Timber.tag("receiveMessage's chatIdInClass").d(it) }

        return chatIdFlow.flatMapLatest {
            messagesRemote.receiveMessage(it).map { message ->
                when (message) {
                    is Result.Success -> Result.Success(message.data.toChattingUiItem())
                    is Result.Error -> Result.Error(message.message)
                    Result.Loading -> Result.Loading
                }
            }
        }
    }

    override suspend fun initMessage(feedId: String, chatId: String): List<Result<ChattingUiItem>> {
//        val chatId = usersRemote.getChatId(userId = uid, feedId = feedId)
        return when (val messages = messagesRemote.initMessage(chatId)) {
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

    fun subscribeChatId(feedId: String): Flow<String> = flow {
        Timber.d("subscribeChatId!! feedId : $feedId")
        emit(usersRemote.getChatId(userId = uid, feedId = feedId))
    }

    override suspend fun getChatId(feedId: String): String {
        return usersRemote.getChatId(userId = uid, feedId = feedId)
    }

//    fun addDatabase(chatList: List<Result<ChattingUiItem>>) {
//        // TODO 레포지토리로 옮겨야 함, 여러 명일 때는 방식을 변경해야 할 필요가 있음
//        val chatRoomDb = ChatRoomDb.getDatabase()
//        CoroutineScope(Dispatchers.IO).launch {
//            for(chat in chatList) {
//                if(chat is Result.Success) {
//                    if(chat.data is ChattingUiItem.MyChatItem) chatRoomDb.chatDao().addChatMessage(
//                        ChatEntity(messageId = chat.data.id, chatId = chatId, message = chat.data.message, sender = uid)
//                    )
//                    else if(chat.data is ChattingUiItem.YourChatItem) chatRoomDb.chatDao().addChatMessage(
//                        ChatEntity(messageId = chat.data.id, chatId = chatId, message = chat.data.message, sender = targetId)
//                    )
//                }
//            }
//        }
//    }

}

fun MessagesInfoModel.toChattingUiItem(): ChattingUiItem {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    Timber.tag("nowTime").d(this.timestamp.toLocalDateTime().toString())
    return if (this.sender == uid) {
        ChattingUiItem.MyChatItem(
            id = this.messageId,
            message = this.message,
            time = this.timestamp.toLocalDateTime()
        )
    } else {
        ChattingUiItem.YourChatItem(
            id = this.messageId,
            message = this.message,
            time = this.timestamp.toLocalDateTime()
        )
    }
}