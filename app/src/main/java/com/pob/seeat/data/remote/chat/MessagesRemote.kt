package com.pob.seeat.data.remote.chat

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pob.seeat.data.model.Result
import com.pob.seeat.data.model.chat.MessageModel
import com.pob.seeat.data.model.chat.MessagesInfoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.Date
import kotlin.coroutines.resume

class MessagesRemote {
    private val firebaseDb = Firebase.database
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val messageRef = firebaseDb.getReference("messages")

    // TODO 나중에 메시지 초기화할 때, 각 메시지마다 Result로 받아서 error가 있는지 확인. 그리고 receiveMessage를 써보는 것도 좋을 듯
    suspend fun initMessage(chatId: String): Result<List<MessagesInfoModel>> {
        val infoList: ArrayList<MessagesInfoModel> = arrayListOf()
        return suspendCancellableCoroutine { continuation ->
            messageRef.child(chatId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (messageSnapshot in snapshot.children) {
                            val message = messageSnapshot.child("message").value.toString()
                            val sender = messageSnapshot.child("sender").value.toString()
                            val timestamp =
                                Timestamp(Date(messageSnapshot.child("timestamp").value as Long))
                            infoList.add(
                                MessagesInfoModel(
                                    message = message,
                                    sender = sender,
                                    timestamp = timestamp
                                )
                            )
                        }
                        Timber.d("initMessages : $infoList")
                        if (continuation.isActive) {
                            continuation.resume(Result.Success(infoList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (continuation.isActive) {
                            continuation.resume(Result.Error(error.message))
                        }
                    }
                })
        }
    }

    fun receiveMessage(chatId: String): Flow<Result<MessagesInfoModel>> {
//        val job = SupervisorJob()
//        val scope = CoroutineScope(Dispatchers.IO + job)
        return callbackFlow {
            val listener = object : ChildEventListener {
                var isInit = true
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(isInit) {
                        isInit = false
                        return
                    }
                    Timber.d("onChildAdded 임 ${snapshot.key}")
                    val message = snapshot.child("message").value.toString()
                    val sender = snapshot.child("sender").value.toString()
                    val timestamp = Timestamp(Date(snapshot.child("timestamp").value as Long))
                    val nowChatModel = MessagesInfoModel(
                        message = message,
                        sender = sender,
                        timestamp = timestamp
                    )
                    Timber.tag("snapshot parent").d(snapshot.ref.parent.toString().substringAfterLast("/"))

                    trySend(
                        Result.Success(
                            nowChatModel
                        )
                    ).isSuccess
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Timber.d("onChildChanged 임")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Timber.d("onChildRemoved 임")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Timber.d("onChildMoved 임")
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.d("onChildCancelled 임")
                    trySend(Result.Error(error.message))
                }

            }
            messageRef.child(chatId).orderByChild("timestamp").limitToLast(1)
                .addChildEventListener(listener)
            awaitClose {
                messageRef.child(chatId).orderByChild("timestamp").limitToLast(1)
                    .removeEventListener(listener)
            }
        }
    }

    suspend fun sendMessage(chatId: String, message: String) : Boolean {
        return suspendCancellableCoroutine { continuation ->
            val messageDb = messageRef.child(chatId)
            val newMessage = messageDb.push()
            val messageMap = hashMapOf(
                "message" to message,
                "sender" to uid,
                "timestamp" to ServerValue.TIMESTAMP,
            )
            newMessage.setValue(messageMap).addOnSuccessListener {
                Timber.d("message success")
                continuation.resume(true)
            }.addOnFailureListener {
                Timber.d("message fail")
                continuation.resume(false)
            }
        }
    }
}