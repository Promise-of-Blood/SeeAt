package com.pob.seeat.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.pob.seeat.data.model.chat.ChatsChattingModel
import com.pob.seeat.data.remote.chat.ChatsRemote
import com.pob.seeat.data.remote.chat.MessagesRemote
import com.pob.seeat.data.remote.chat.UsersRemote
import com.pob.seeat.domain.repository.ChatListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.chat.ChatModel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

class ChatListRepositoryImpl @Inject constructor(
    private val chatsRemote: ChatsRemote,
    private val messagesRemote: MessagesRemote,
    private val usersRemote: UsersRemote,
) : ChatListRepository {
    override fun receiveChatList(): Flow<Result<ChatModel>> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        return usersRemote.getChatIdListFromUser(userId ?: "")
            .flatMapLatest { chatIdList ->
                Timber.d("chatReceiveList: $chatIdList")
                when (chatIdList) {
                    is Result.Success -> {
                        chatIdList.data.map { chatId ->
                            Timber.d("chatReceiveId: $chatId")
                            chatsRemote.receiveChat(chatId)
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