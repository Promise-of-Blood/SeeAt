package com.pob.seeat.data.model

import com.google.firebase.Timestamp

data class ChatModel (
    val message : String?,
    val sender : String?,
    val receiver : String?,
    val timestamp : Timestamp?,
)