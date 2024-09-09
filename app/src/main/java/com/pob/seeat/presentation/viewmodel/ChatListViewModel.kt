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
import com.pob.seeat.utils.Utils.toKoreanDiffString
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatListRepositoryImpl: ChatListRepository,
) : ViewModel() {
    private val _chatList = MutableStateFlow<List<Result<ChatListUiItem>>>(listOf())
    val chatList: StateFlow<List<Result<ChatListUiItem>>> = _chatList

    fun receiveChatList() {
        val list = _chatList.value.toList()
        val updatedList = list.toMutableList()
        viewModelScope.launch {
            chatListRepositoryImpl.receiveChatList().collectLatest { it ->
                Timber.d("receiveChatList: $it")
                when (it) {
                    is Result.Success -> {
                        if (updatedList.isEmpty()) {
                            updatedList.add(
                                Result.Success(
                                    ChatListUiItem(
                                        id = it.data.chatId,
                                        personId = "",
                                        person = "",
                                        icon = "",
                                        content = it.data.chatInfo.lastMessage,
                                        lastTime = it.data.chatInfo.whenLast,
                                        unreadMessageCount = 0,
                                        feedFrom = it.data.chatInfo.feedFrom
                                    )
                                )
                            )
                        } else {
                            var isExist = false
                            for (i in updatedList.indices) {
                                if (updatedList[i] is Result.Success && (updatedList[i] as Result.Success<ChatListUiItem>).data.id == it.data.chatId) {
                                    updatedList[i] =
                                        Result.Success(
                                            ChatListUiItem(
                                                id = it.data.chatId,
                                                personId = "",
                                                person = "",
                                                icon = "",
                                                content = it.data.chatInfo.lastMessage,
                                                lastTime = it.data.chatInfo.whenLast,
                                                unreadMessageCount = 0,
                                                feedFrom = it.data.chatInfo.feedFrom
                                            )
                                        )
                                    isExist = true
                                    Timber.d("receiveChatListAdded: $it")
                                    break
                                }
                            }
                            if(!isExist) {
                                updatedList.add(
                                    Result.Success(
                                        ChatListUiItem(
                                            id = it.data.chatId,
                                            personId = "",
                                            person = "",
                                            icon = "",
                                            content = it.data.chatInfo.lastMessage,
                                            lastTime = it.data.chatInfo.whenLast,
                                            unreadMessageCount = 0,
                                            feedFrom = it.data.chatInfo.feedFrom
                                        )
                                    )
                                )
                            }

                        }

                    }

                    is Result.Error -> {
                        updatedList.add(Result.Error(it.message))
                    }

                    is Result.Loading -> {
                        updatedList.add(Result.Loading)
                    }
                }
                _chatList.value = updatedList
                chatList.collect { collected ->
                    Timber.d("chatListCollect? : $collected")
                }
                Timber.d("chatList: ${_chatList.value}")
            }
        }
    }
}