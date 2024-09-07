package com.pob.seeat.data.model.chat

import com.google.firebase.Timestamp

data class ChatModel(
    val userId: String,
    val chatIdList: Map<String, ChatsChattingModel>,
)

data class ChatsChattingModel(
    val feedId: String,
    val lastMessage: String,
    val whenLast: Timestamp,
    val users: List<String>,
)
