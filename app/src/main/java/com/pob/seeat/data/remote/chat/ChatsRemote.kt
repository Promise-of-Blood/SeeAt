package com.pob.seeat.data.remote.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.chat.ChatModel
import com.pob.seeat.data.model.chat.ChatsChattingModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ChatsRemote {
    private val firebaseDb = Firebase.database
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val chatRef = firebaseDb.getReference("chats")

    fun createChat(chat: ChatsChattingModel): String {
        val chatId = chatRef.push()
        saveChat(chat, chatId)
        return chatId.key ?: ""
    }

    private fun saveChat(chat: ChatsChattingModel, chatId: DatabaseReference) {
        chatId.apply {
            child("feedFrom").setValue(chat.feedFrom)
            child("lastMessage").setValue(chat.lastMessage)
            child("whenLast").setValue(chat.whenLast)
            for(user in chat.userList) {
                child("users").child(user).setValue(true)
            }
        }
    }

    fun saveChat(chat: ChatsChattingModel, chatId: String) {
        chatRef.child(chatId).apply {
            child("feedFrom").setValue(chat.feedFrom)
            child("lastMessage").setValue(chat.lastMessage)
            child("whenLast").setValue(chat.whenLast)
            // TODO 매번 유저 리스트를 업데이트 하지 말고, 유저 리스트의 변화가 있는지 보고 업데이트 하는 식으로 변경
            for(user in chat.userList) {
                child("users").child(user).setValue(true)
            }
        }
    }

    suspend fun getChat(chatId: String): ChatsChattingModel {
        return suspendCancellableCoroutine { continuation ->
            chatRef.child(chatId).get().addOnSuccessListener {
                continuation.resume(
                    ChatsChattingModel(
                        feedFrom = it.child("feedFrom").value.toString(),
                        lastMessage = it.child("lastMessage").value.toString(),
                        whenLast = it.child("whenLast").value as Long,
                        userList = (it.child("users").value as Map<String, Boolean>).keys.toList()
                    )
                )
            }
        }
    }

    fun receiveChat(chatId: String): Flow<Result<ChatModel>> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usersMap = snapshot.child("users").value as Map<String, Boolean>
                    trySend(
                        Result.Success(
                            ChatModel(
                                chatId = snapshot.key ?: "",
                                chatInfo = ChatsChattingModel(
                                    feedFrom = snapshot.child("feedFrom").value.toString(),
                                    lastMessage = snapshot.child("lastMessage").value.toString(),
                                    whenLast = snapshot.child("whenLast").value as Long,
                                    userList = usersMap.keys.toList()
                                )
                            )
                        )
                    ).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.Error(error.message))
                }
            }
            chatRef.child(chatId).addValueEventListener(listener)
            awaitClose {
                chatRef.child(chatId).removeEventListener(listener)
            }
        }
    }

}