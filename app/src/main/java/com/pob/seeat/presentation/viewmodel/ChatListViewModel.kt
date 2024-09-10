package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.pob.seeat.domain.repository.ChatListRepository
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.chat.ChatModel
import com.pob.seeat.utils.Utils.toKoreanDiffString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatListRepositoryImpl: ChatListRepository,
) : ViewModel() {
    private val _chatList = MutableStateFlow<List<Result<ChatListUiItem>>>(listOf())
    val chatList: StateFlow<List<Result<ChatListUiItem>>> = _chatList

    private var newChat : Flow<Result<ChatModel>> = flowOf()

    suspend fun receiveChatList() {
        newChat = chatListRepositoryImpl.receiveChatList()
        viewModelScope.launch {
            launch {
                chatList.collect { collected ->
                    Timber.d("chatListCollect? : $collected")
                }
            }
            newChat.flowOn(Dispatchers.IO).collectLatest { chat ->
                val list = _chatList.value.toList()
                val updatedList = list.toMutableList()
                Timber.d("receiveChatList: $chat")
                when (chat) {
                    is Result.Success -> {
                        if (updatedList.isEmpty()) {
                            updatedList.add(
                                Result.Success(
                                    ChatListUiItem(
                                        id = chat.data.chatId,
                                        personId = "",
                                        person = "",
                                        icon = "",
                                        content = chat.data.chatInfo.lastMessage,
                                        lastTime = chat.data.chatInfo.whenLast,
                                        unreadMessageCount = 0,
                                        feedFrom = chat.data.chatInfo.feedFrom
                                    )
                                )
                            )
                        } else {
                            var isExist = false
                            for (i in updatedList.indices) {
                                if (updatedList[i] is Result.Success && (updatedList[i] as Result.Success<ChatListUiItem>).data.id == chat.data.chatId) {
                                    updatedList[i] =
                                        Result.Success(
                                            ChatListUiItem(
                                                id = chat.data.chatId,
                                                personId = "",
                                                person = "",
                                                icon = "",
                                                content = chat.data.chatInfo.lastMessage,
                                                lastTime = chat.data.chatInfo.whenLast,
                                                unreadMessageCount = 0,
                                                feedFrom = chat.data.chatInfo.feedFrom
                                            )
                                        )
                                    isExist = true
                                    Timber.d("receiveChatListAdded: $chat")
                                    break
                                }
                            }
                            if(!isExist) {
                                updatedList.add(
                                    Result.Success(
                                        ChatListUiItem(
                                            id = chat.data.chatId,
                                            personId = "",
                                            person = "",
                                            icon = "",
                                            content = chat.data.chatInfo.lastMessage,
                                            lastTime = chat.data.chatInfo.whenLast,
                                            unreadMessageCount = 0,
                                            feedFrom = chat.data.chatInfo.feedFrom
                                        )
                                    )
                                )
                            }

                        }

                    }

                    is Result.Error -> {
                        updatedList.add(Result.Error(chat.message))
                    }

                    is Result.Loading -> {
                        updatedList.add(Result.Loading)
                    }
                }
                _chatList.value = updatedList
                _chatList.emit(updatedList)
                Timber.d("chatList: ${_chatList.value}")
                Timber.d("ViewModel chatList: $chatList")
            }
        }
    }
}