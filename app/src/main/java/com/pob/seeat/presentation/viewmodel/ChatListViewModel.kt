package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            val list = _chatList.value.toMutableList()
            chatListRepositoryImpl.receiveChatList().collectLatest {
                Timber.d("receiveChatListInCollect")
                when(it) {
                    is Result.Success -> {
                        list.add(
                            Result.Success(ChatListUiItem(
                                personId = "",
                                person = "",
                                icon = "",
                                content = it.data.lastMessage,
                                lastTime = it.data.whenLast.toKoreanDiffString(),
                                unreadMessageCount = 0
                            ))
                        )
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