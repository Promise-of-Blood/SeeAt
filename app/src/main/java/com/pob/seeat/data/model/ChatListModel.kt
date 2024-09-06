package com.pob.seeat.data.model

import com.google.firebase.Timestamp

data class ChatListModel (
    val feedFrom : String?,
    val lastMessage : String?,
    val whenLast : Timestamp?,
)