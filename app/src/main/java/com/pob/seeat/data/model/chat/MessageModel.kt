package com.pob.seeat.data.model.chat

import com.google.firebase.Timestamp

data class MessageModel (
    val chatId: String,
    val messageList : List<MessagesInfoModel>
)

data class MessagesInfoModel (
    val message: String,
    val receiverId: String,
    val senderId: String,
    val timestamp: Timestamp
)