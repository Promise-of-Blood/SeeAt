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
import com.pob.seeat.presentation.view.chat.chatlist.ChatListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatListRepositoryImpl: ChatListRepository,
) : ViewModel() {
    private val _chatList = MutableStateFlow<ChatListUiState<List<Result<ChatListUiItem>>>>(ChatListUiState.Loading)
    val chatList: StateFlow<ChatListUiState<List<Result<ChatListUiItem>>>> = _chatList

    private var newChat : Flow<Result<ChatListUiItem>> = flowOf()

    suspend fun receiveChatList() {
        newChat = chatListRepositoryImpl.receiveChatList()
        val endLoadingDelayJob = viewModelScope.async<Boolean> {
            delay(5000)
            true
        }
        var isLoading = true
        viewModelScope.launch {
            newChat.flowOn(Dispatchers.IO).collectLatest { chat ->
                isLoading = false
                val chatListUiState = _chatList.value
                val list = if(chatListUiState is ChatListUiState.Success) chatListUiState.data.toList() else listOf()
                val updatedList = list.toMutableList()
                Timber.d("receiveChatList: $chat")
                when (chat) {
                    is Result.Success -> {
                        if (updatedList.isEmpty()) {
                            updatedList.add(
                                chat
                            )
                        } else {
                            var isExist = false
                            for (i in updatedList.indices) {
                                if (updatedList[i] is Result.Success && (updatedList[i] as Result.Success<ChatListUiItem>).data.id == chat.data.id) {
                                    updatedList[i] =
                                        chat
                                    isExist = true
                                    Timber.d("receiveChatListAdded: $chat")
                                    break
                                }
                            }
                            if(!isExist) {
                                updatedList.add(
                                    chat
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

                    else -> {}
                }
                _chatList.value =
                    when {
                        updatedList.isEmpty() -> ChatListUiState.Empty
                        updatedList.filterIsInstance<Result.Loading>().isNotEmpty() -> ChatListUiState.Loading
                        updatedList.filterIsInstance<Result.Error>().isNotEmpty() -> ChatListUiState.Error("error")
                        else -> ChatListUiState.Success(updatedList)
                    }
//                endLoadingDelayJob.cancel()
//                    if(updatedList.isEmpty()) ChatListUiState.Empty
//                    else ChatListUiState.Success(updatedList)
//                _chatList.emit(updatedList)
                Timber.d("chatList: ${_chatList.value}")
                Timber.d("ViewModel chatList: $chatList")
            }
        }
        Timber.d("ViewModel chatList check: ${chatList.value}")
        if(awaitAll(endLoadingDelayJob).all { it } && isLoading) {
            _chatList.value = ChatListUiState.Empty
        }
    }
}