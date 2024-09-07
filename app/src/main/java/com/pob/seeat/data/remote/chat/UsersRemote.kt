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
        var chatId = "error"
        userRef.child(userId).child(feedId).get().addOnSuccessListener {
            chatId = it.value as String
        }
        return chatId
    }

    fun saveUser(user: UserModel) {
        userRef.child(user.userId).setValue(user)
    }

}