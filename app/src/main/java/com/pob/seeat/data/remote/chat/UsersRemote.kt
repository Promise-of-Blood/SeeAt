package com.pob.seeat.data.remote.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pob.seeat.data.model.chat.UserModel

class UsersRemote {
    private val firebaseDb = Firebase.database
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val userRef = firebaseDb.getReference("users")

    fun getChatId(userId: String, feedId: String) : String {
        var chatId = "none"
        userRef.child(userId).get().addOnSuccessListener {
            if(!it.hasChild(feedId)) return@addOnSuccessListener
            it.child(feedId).children.forEach { ele ->
                chatId = ele.value as String
            }
        }
        return chatId
    }

    fun createUserChat(feedId: String, chatId: String) {
        val userId = uid?.let { userRef.child(it) }
        val userFeed = userId?.child(feedId)
        userFeed?.get()?.addOnSuccessListener {
            userFeed.setValue(chatId)
        }
    }

    fun getChatIdListFromUser(userId: String) : List<String> {
        var values : List<String> = listOf()
        firebaseDb.getReference("users").child(userId).get().addOnSuccessListener { user ->
            values = user.children.map { it.value as String }
        }
        return values
    }

}