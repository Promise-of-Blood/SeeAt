package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.ChatListModel
import com.pob.seeat.data.model.ChatModel
import com.pob.seeat.domain.repository.ChatRepository
import com.pob.seeat.presentation.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepositoryImpl: ChatRepository
) : ViewModel() {
    val _chatResult = MutableStateFlow<List<Result<ChattingUiItem>>>(listOf())
    val chatResult: StateFlow<List<Result<ChattingUiItem>>> get() = _chatResult

    var newMessage : Flow<Result<ChattingUiItem>> = flowOf()

    suspend fun initMessage(feedId: String) {
        viewModelScope.launch {
            chatRepositoryImpl.initMessage(feedId)
                .flowOn(Dispatchers.IO)
                .collectLatest { list ->
                    Timber.d(list.toString())
                    _chatResult.value = list.toMutableList()
                }
        }
    }

    fun subscribeMessage(feedId: String) {
        newMessage = chatRepositoryImpl.receiveMessage(feedId)
        viewModelScope.launch {
            newMessage.flowOn(Dispatchers.IO).collectLatest {
                val list = _chatResult.value.toMutableList()
                Timber.tag("Subscribe Message ViewModel Before").d(it.toString())
                Timber.tag("Subscribe Message ViewModel Before").d("list : $list")
                Timber.tag("Subscribe Message ViewModel Before").d("chatResult : ${_chatResult.value}")
                list.add(it)
                _chatResult.value = list
                Timber.tag("Subscribe Message ViewModel After").d(it.toString())
                Timber.tag("Subscribe Message ViewModel After").d("list : $list")
                Timber.tag("Subscribe Message ViewModel After").d("chatResult : ${_chatResult.value}")
            }
        }
    }

    suspend fun sendMessage(targetUid: String, feedId: String, message: String) {
        chatRepositoryImpl.sendMessage(targetUid, feedId, message)
    }

}