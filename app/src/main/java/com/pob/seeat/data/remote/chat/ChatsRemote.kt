package com.pob.seeat.data.remote.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pob.seeat.data.model.chat.ChatModel
import com.pob.seeat.data.model.chat.ChatsChattingModel

class ChatsRemote {
    private val firebaseDb = Firebase.database
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val chatRef = firebaseDb.getReference("chats")

    fun createChat(chat: ChatsChattingModel) : String {
        val chatId = chatRef.push()
        saveChat(chat, chatId)
        return chatId.key ?: ""
    }

    fun saveChat(chat: ChatsChattingModel, chatId: DatabaseReference) {
        chatId.apply {
            child("feedFrom").setValue(chat.feedFrom)
            child("lastMessage").setValue(chat.lastMessage)
            child("whenLast").setValue(chat.whenLast)
            child("users").setValue(chat.users)
        }
    }

    fun saveChat(chat: ChatsChattingModel, chatId: String) {
        chatRef.child(chatId).apply {
            child("feedFrom").setValue(chat.feedFrom)
            child("lastMessage").setValue(chat.lastMessage)
            child("whenLast").setValue(chat.whenLast)
            child("users").setValue(chat.users)
        }
    }


}