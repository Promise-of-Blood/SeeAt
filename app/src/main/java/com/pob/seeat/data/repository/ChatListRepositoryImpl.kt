package com.pob.seeat.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.pob.seeat.data.remote.chat.ChatsRemote
import com.pob.seeat.data.remote.chat.MessagesRemote
import com.pob.seeat.data.remote.chat.UsersRemote
import com.pob.seeat.domain.repository.ChatListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.chat.ChatFeedInfoModel
import com.pob.seeat.data.model.chat.ChatModel
import com.pob.seeat.data.remote.FeedRemote
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ChatListRepositoryImpl @Inject constructor(
    private val chatsRemote: ChatsRemote,
    private val messagesRemote: MessagesRemote,
    private val usersRemote: UsersRemote,
    private val feedRemote: FeedRemote,
) : ChatListRepository {
    override fun receiveChatList(): Flow<Result<ChatListUiItem>> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        return usersRemote.getChatIdListFromUser(userId = userId ?: "")
            .flatMapLatest { chatIdList ->
                Timber.d("chatReceiveList: $chatIdList")
                when (chatIdList) {
                    is Result.Success -> {
                        chatIdList.data.map { chatId ->
                            Timber.d("chatReceiveId: $chatId")
                            chatsRemote.receiveChat(chatId).map {
                                when(it) {
                                    is Result.Success -> {
                                        Result.Success(it.data.toChatListUiItem(feedRemote.getUserByFeedId(it.data.chatInfo.feedFrom)))
                                    }
                                    is Result.Error -> {
                                        Result.Error(it.message)
                                    }
                                    is Result.Loading -> {
                                        Result.Loading
                                    }
                                }
                            }
                        }.asFlow().flattenMerge()
                    }

                    is Result.Error -> {
                        flowOf(Result.Error(chatIdList.message))
                    }

                    is Result.Loading -> {
                        flowOf(Result.Loading)
                    }
                }
            }
    }
}

fun ChatModel.toChatListUiItem(chatInfo : ChatFeedInfoModel): ChatListUiItem {
    return ChatListUiItem(
        id = this.chatId,
        person = chatInfo.nickname ?: "(알 수 없음)",
        icon = chatInfo.profileUrl ?: "",
        content = this.chatInfo.lastMessage,
        lastTime = this.chatInfo.whenLast,
        unreadMessageCount = 0,
        feedFrom = this.chatInfo.feedFrom,
    )
}