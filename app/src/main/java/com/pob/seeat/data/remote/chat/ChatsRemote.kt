package com.pob.seeat.data.remote.chat

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.chat.ChatModel
import com.pob.seeat.data.model.chat.ChatsChattingModel
import com.pob.seeat.data.model.chat.MessagesInfoModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.Date
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

    fun saveChat(chat: ChatsChattingModel, chatId: DatabaseReference) {
        chatId.apply {
            child("feedFrom").setValue(chat.feedFrom)
            child("lastMessage").setValue(chat.lastMessage)
            child("whenLast").setValue(chat.whenLast)
        }
    }

    fun saveChat(chat: ChatsChattingModel, chatId: String) {
        chatRef.child(chatId).apply {
            child("feedFrom").setValue(chat.feedFrom)
            child("lastMessage").setValue(chat.lastMessage)
            child("whenLast").setValue(chat.whenLast)
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
                    )
                )
            }
        }
    }

    fun receiveChat(chatId: String): Flow<Result<ChatModel>> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(
                        Result.Success(
                            ChatModel(
                                chatId = snapshot.key ?: "",
                                chatInfo = ChatsChattingModel(
                                    feedFrom = snapshot.child("feedFrom").value.toString(),
                                    lastMessage = snapshot.child("lastMessage").value.toString(),
                                    whenLast = snapshot.child("whenLast").value as Long,
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