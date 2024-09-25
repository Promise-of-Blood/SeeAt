package com.pob.seeat.data.database.chat

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat")
    fun getAll(): List<ChatEntity>

    @Query("SELECT COUNT(*) FROM chat WHERE chatId = :chatId")
    fun getChatCount(chatId : String): Int

    @Query("INSERT INTO chat (chatId, message, sender) VALUES (:chatId, :message, :sender)")
    fun addChatMessage(chatId: String, message: String, sender: String)
}