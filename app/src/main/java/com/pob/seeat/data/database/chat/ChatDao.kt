package com.pob.seeat.data.database.chat

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat")
    fun getAll(): List<ChatEntity>

    @Query("SELECT COUNT(*) FROM chat WHERE chatId = :chatId")
    fun getChatCount(chatId : String): Int

    @Upsert
    fun addChatMessage(chatEntity: ChatEntity)
}