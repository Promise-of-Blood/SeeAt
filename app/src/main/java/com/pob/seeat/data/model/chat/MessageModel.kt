package com.pob.seeat.data.model.chat

import com.google.firebase.Timestamp

data class MessageModel (
    val chatId: String,
    val messageList : List<MessagesInfoModel>
)

data class MessagesInfoModel (
    val messageId: String,
    val message: String,
    val sender: String,
    val timestamp: Timestamp
)