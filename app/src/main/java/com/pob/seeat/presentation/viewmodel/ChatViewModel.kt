package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepositoryImpl: ChatRepository
) : ViewModel() {
    private val _chatResult = MutableStateFlow<List<Result<ChattingUiItem>>>(listOf())
    val chatResult: StateFlow<List<Result<ChattingUiItem>>> get() = _chatResult

    private var newMessage : Flow<Result<ChattingUiItem>> = flowOf()

    suspend fun initMessage(feedId: String, chatId: String) {
        viewModelScope.launch {
            Timber.tag("initMessage?").d("initMessage is On")
            val list = chatRepositoryImpl.initMessage(feedId = feedId, chatId = chatId)
            val mutableList : MutableList<Result<ChattingUiItem>> = mutableListOf()
            var prevMessageTime = LocalDateTime.of(1900, 1, 1, 0, 0, 0)
            for(ele in list) {
                if(ele is Result.Success) {
                    when(ele.data) {
                        is ChattingUiItem.MyChatItem -> {
                            if(!(prevMessageTime compareWith ele.data.time)) {
                                mutableList.add(Result.Success(ChattingUiItem.OnlyTimeItem(id = ele.data.time.toString(), time = ele.data.time)))
                            }
                            Timber.tag("PrevMessageTime").d("$prevMessageTime ${ele.data.time}")
                            prevMessageTime = ele.data.time
                        }
                        is ChattingUiItem.YourChatItem -> {
                            if(!(prevMessageTime compareWith ele.data.time)) {
                                mutableList.add(Result.Success(ChattingUiItem.OnlyTimeItem(id = ele.data.time.toString(), time = ele.data.time)))
                            }
                            Timber.tag("PrevMessageTime").d("$prevMessageTime ${ele.data.time}")
                            prevMessageTime = ele.data.time
                        }
                        is ChattingUiItem.OnlyTimeItem -> {}
                    }
                }
                mutableList.add(ele)
            }
            Timber.tag("initMessage MutableList").d("mutableList : $mutableList")
            _chatResult.value = mutableList.setShowTime()
        }
    }

    suspend fun subscribeMessage(feedId: String, chatId: String) {
        Timber.tag("subscribeMessage").d("subscribeMessage is On")
        if(chatId != "none") {
            newMessage = chatRepositoryImpl.receiveMessage(feedId = feedId, chatId = chatId)
            viewModelScope.launch {
                var prevMessageTime = LocalDateTime.of(1900, 1, 1, 0, 0, 0)
                newMessage.flowOn(Dispatchers.IO).collectLatest {
                    val list = _chatResult.value.toMutableList()
                    if(list.lastOrNull() is Result.Success) {
                        val tempData = (list.lastOrNull() as Result.Success<ChattingUiItem>).data
                        if(tempData is ChattingUiItem.MyChatItem) prevMessageTime = tempData.time
                        else if(tempData is ChattingUiItem.YourChatItem) prevMessageTime = tempData.time
                    }
                    Timber.tag("Subscribe Message ViewModel Before").d(it.toString())
                    Timber.tag("Subscribe Message ViewModel Before").d("list : $list")
                    Timber.tag("Subscribe Message ViewModel Before").d("chatResult : ${_chatResult.value}")
                    if(it is Result.Success) {
                        Timber.d("subscribeMessage Success")
                        if(it.data is ChattingUiItem.MyChatItem) {
                            if(!(prevMessageTime compareWith it.data.time)) {
                                list.add(Result.Success(ChattingUiItem.OnlyTimeItem(id = it.data.time.toString(), time = it.data.time)))
                            }
                            prevMessageTime = it.data.time
                        }
                        else if(it.data is ChattingUiItem.YourChatItem) {
                            if(!(prevMessageTime compareWith it.data.time)) {
                                list.add(Result.Success(ChattingUiItem.OnlyTimeItem(id = it.data.time.toString(), time = it.data.time)))
                            }
                            prevMessageTime = it.data.time
                        }
                    }
                    list.add(it)
                    _chatResult.value = list.setShowTimeWhenAdd()
                    _chatResult.emit(list.setShowTimeWhenAdd())
                    Timber.tag("Subscribe Message ViewModel After").d(it.toString())
                    Timber.tag("Subscribe Message ViewModel After").d("list : $list")
                    Timber.tag("Subscribe Message ViewModel After").d("chatResult : ${_chatResult.value}")
                }
            }
        }
    }

    suspend fun sendMessage(targetUid: String, feedId: String, message: String, chatId: String): Boolean {
        return chatRepositoryImpl.sendMessage(
            targetUid = targetUid,
            feedId = feedId,
            message = message,
            chatId = chatId
        )
    }

    suspend fun getChatId(feedId: String) : String {
        return chatRepositoryImpl.getChatId(feedId = feedId)
    }

    // TODO Room Paging 쓰면 살짝 바꿔야 할 듯?
    suspend fun List<Result<ChattingUiItem>>.setShowTime() : List<Result<ChattingUiItem>> {
        val mutableList = this.toMutableList()
        for(i in 0 until mutableList.size - 1) {
            if(mutableList[i] is Result.Success && mutableList[i + 1] is Result.Success) {
                var prev = (mutableList[i] as Result.Success).data
                val next = (mutableList[i + 1] as Result.Success).data
                when(prev) {
                    is ChattingUiItem.MyChatItem -> {
                        when(next) {
                            is ChattingUiItem.MyChatItem -> {
                                if(compareTime(prev.time, next.time)) {
                                    prev = prev.copy(isShowTime = false)
                                }
                            }
                            is ChattingUiItem.OnlyTimeItem -> {}
                            is ChattingUiItem.YourChatItem -> {}
                        }
                    }
                    is ChattingUiItem.YourChatItem -> {
                        when(next) {
                            is ChattingUiItem.MyChatItem -> {}
                            is ChattingUiItem.OnlyTimeItem -> {}
                            is ChattingUiItem.YourChatItem -> {
                                // TODO 나중에 여러 명이 채팅하면, compareSender를 추가해야 함
                                if(compareTime(prev.time, next.time)) {
                                    prev = prev.copy(isShowTime = false)
                                }
                            }
                        }
                    }
                    is ChattingUiItem.OnlyTimeItem -> {}
                }
                mutableList[i] = Result.Success(prev)
            }
        }
        return mutableList
    }

    suspend fun List<Result<ChattingUiItem>>.setShowTimeWhenAdd() : List<Result<ChattingUiItem>> {
        val mutableList = this.toMutableList()
        if(mutableList[mutableList.size - 1] is Result.Success && mutableList[mutableList.size - 2] is Result.Success) {
            var prev = (mutableList[mutableList.size - 2] as Result.Success).data
            val next = (mutableList[mutableList.size - 1] as Result.Success).data
            when(prev) {
                is ChattingUiItem.MyChatItem -> {
                    when(next) {
                        is ChattingUiItem.MyChatItem -> {
                            if(compareTime(prev.time, next.time)) {
                                prev = prev.copy(isShowTime = false)
                            }
                        }
                        is ChattingUiItem.OnlyTimeItem -> {}
                        is ChattingUiItem.YourChatItem -> {}
                    }
                }
                is ChattingUiItem.YourChatItem -> {
                    when(next) {
                        is ChattingUiItem.MyChatItem -> {}
                        is ChattingUiItem.OnlyTimeItem -> {}
                        is ChattingUiItem.YourChatItem -> {
                            // TODO 나중에 여러 명이 채팅하면, compareSender를 추가해야 함
                            if(compareTime(prev.time, next.time)) {
                                prev = prev.copy(isShowTime = false)
                            }
                        }
                    }
                }
                is ChattingUiItem.OnlyTimeItem -> {}
            }
            mutableList[mutableList.size - 2] = Result.Success(prev)
        }
        return mutableList
    }

    fun compareTime(time1: LocalDateTime, time2: LocalDateTime) : Boolean = (time1.hour == time2.hour) && (time1.minute == time2.minute)

    fun compareSender(sender1: String, sender2: String) : Boolean = sender1 == sender2

    infix fun LocalDateTime.compareWith(targetTime: LocalDateTime) : Boolean {
        val isSame = this.year == targetTime.year && this.month == targetTime.month && this.dayOfMonth == targetTime.dayOfMonth
        Timber.tag("compareWith").d("$this && $targetTime $isSame")
        return isSame
    }

}