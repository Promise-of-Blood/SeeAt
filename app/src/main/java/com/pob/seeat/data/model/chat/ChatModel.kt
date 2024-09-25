package com.pob.seeat.data.model.chat

import com.google.firebase.Timestamp

data class ChatModel(
    val chatId: String,
    val chatInfo: ChatsChattingModel,
)

data class ChatsChattingModel(
    val feedFrom: String,
    val lastMessage: String,
    val whenLast: Long,
    val userList: List<String>,
    val sender: String,
)
