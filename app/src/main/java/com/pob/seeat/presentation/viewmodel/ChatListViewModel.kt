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
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatListRepositoryImpl: ChatListRepository
): ViewModel() {
    private val _chatList = MutableStateFlow<List<Result<ChatListUiItem>>>(listOf())
    val chatList: StateFlow<List<Result<ChatListUiItem>>> get() = _chatList

    fun receiveChatList() {
        Timber.d("receiveChatList")
        viewModelScope.launch {
            Timber.d("receiveChatListInScope")
            chatListRepositoryImpl.receiveChatList().collectLatest {
                val list = _chatList.value.toMutableList()
                Timber.d("receiveChatListInCollect")
                when(it) {
                    is Result.Success -> {
                        if(list.isEmpty()) {
                            list.add(
                                Result.Success(ChatListUiItem(
                                    id = it.data.chatId,
                                    personId = "",
                                    person = "",
                                    icon = "",
                                    content = it.data.chatInfo.lastMessage,
                                    lastTime = Timestamp(it.data.chatInfo.whenLast / 1000,
                                        ((it.data.chatInfo.whenLast % 1000L) * 1000000L).toInt()
                                    ).toKoreanDiffString(),
                                    unreadMessageCount = 0,
                                    feedFrom = it.data.chatInfo.feedFrom
                                ))
                            )
                        }
                        for(i in list.indices) {
                            Timber.d("$i receiveIsLOOP")
                            if(list[i] is Result.Success) {
                                Timber.d("receiveIsSuccess")
                                if((list[i] as Result.Success<ChatListUiItem>).data.id != it.data.chatId) {
                                    Timber.d("receiveIsEqual")
                                    list.add(
                                        Result.Success(ChatListUiItem(
                                            id = it.data.chatId,
                                            personId = "",
                                            person = "",
                                            icon = "",
                                            content = it.data.chatInfo.lastMessage,
                                            lastTime = Timestamp(it.data.chatInfo.whenLast / 1000,
                                                ((it.data.chatInfo.whenLast % 1000L) * 1000000L).toInt()
                                            ).toKoreanDiffString(),
                                            unreadMessageCount = 0,
                                            feedFrom = it.data.chatInfo.feedFrom
                                        ))
                                    )
                                } else {
                                    Timber.d("receiveIsNotEqual")
                                    list[i] = (
                                        Result.Success(ChatListUiItem(
                                            id = it.data.chatId,
                                            personId = "",
                                            person = "",
                                            icon = "",
                                            content = it.data.chatInfo.lastMessage,
                                            lastTime = Timestamp(it.data.chatInfo.whenLast / 1000,
                                                ((it.data.chatInfo.whenLast % 1000L) * 1000000L).toInt()
                                            ).toKoreanDiffString(),
                                            unreadMessageCount = 0,
                                            feedFrom = it.data.chatInfo.feedFrom
                                        ))
                                    )
                                }
                            }
                        }

                    }
                    is Result.Error -> {
                        list.add(Result.Error(it.message))
                    }
                    is Result.Loading -> {
                        list.add(Result.Loading)
                    }
                }
                _chatList.value = list
                Timber.d("chatList: ${_chatList.value}")
            }
        }
    }
}