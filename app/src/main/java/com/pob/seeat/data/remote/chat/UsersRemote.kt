package com.pob.seeat.data.remote.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import com.pob.seeat.data.model.Result

class UsersRemote {
    private val firebaseDb = Firebase.database
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val userRef = firebaseDb.getReference("users")

    suspend fun getChatId(userId: String, feedId: String) : String {
        return suspendCancellableCoroutine { continuation ->
            var chatId = "none"
            Timber.tag("getUserId!!").d(userId)
            Timber.tag("getFeedId!!").d(feedId)
            userRef.child(userId).get().addOnSuccessListener {
                if(!it.hasChild(feedId)) {
                    Timber.tag("getChatIdWhenNone!").d(chatId)
                    continuation.resume(chatId)
                } else {
                    val chatIdMap = it.child(feedId).value as? Map<String, String>
                    chatId = chatIdMap?.keys?.first().toString()
                    Timber.tag("getChatIdWhenNotNone").d(chatId)
                    continuation.resume(chatId)
                }
            }.addOnFailureListener {
                Timber.tag("getChatIdFailure").d(it)
                continuation.resume(chatId)
            }
        }

    }

    fun createUserChat(feedId: String, chatId: String, userId: String) {
        val userFeed = userRef.child(userId).child(feedId)
        userFeed.get().addOnSuccessListener {
            val target = userFeed.child(chatId)
            target.setValue(true)
        }
    }

    fun getChatIdListFromUser(userId: String) : Flow<Result<List<String>>> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatIdList = mutableListOf<String>()
                    snapshot.children.forEach { postSnapshot ->
                        val chatIdMap = postSnapshot.value as? Map<String, String>
                        if (chatIdMap != null) {
                            chatIdList.addAll(chatIdMap.keys)
                        }
                    }
                    trySend(Result.Success(chatIdList)).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.Error(error.message))
                }
            }

            userRef.child(userId).addValueEventListener(listener)
            awaitClose {
                userRef.child(userId).removeEventListener(listener)
            }
        }
    }

}