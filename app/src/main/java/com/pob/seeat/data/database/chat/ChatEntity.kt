package com.pob.seeat.data.database.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat")
data class ChatEntity(
    @PrimaryKey(autoGenerate = false)
    val chatId: String,
    val message: String,
    val sender: String,
)
