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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepositoryImpl: ChatRepository
) : ViewModel() {
    private val _chatResult = MutableStateFlow<MutableList<Result<ChatModel>>>(mutableListOf())
    val chatResult: StateFlow<MutableList<Result<ChatModel>>> get() = _chatResult

    suspend fun initMessage(feedId: String) {
        viewModelScope.launch {
            chatRepositoryImpl.initMessage(feedId)
                .flowOn(Dispatchers.IO)
                .collect { list ->
                    _chatResult.value = list.toMutableList()
                }
        }
    }

    suspend fun receiveMessage(feedId: String) {
        chatRepositoryImpl.receiveMessage(feedId).collect {
            _chatResult.value.add(it)
        }
    }

    suspend fun sendMessage(targetUid: String, feedId: String, message: String) {
        chatRepositoryImpl.sendMessage(targetUid, feedId, message)
    }

}