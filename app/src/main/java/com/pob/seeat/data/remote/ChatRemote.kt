package com.pob.seeat.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.snapshots
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pob.seeat.utils.GoogleAuthUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import kotlin.coroutines.resume

class ChatRemote @Inject constructor(
    val firebase: Firebase,
) {
    // TODO 필요한 거 Hilt로 넘겨버려도 될 듯
    private val firebaseDb = firebase.database
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val chatRef = firebaseDb.getReference("chats")
    private val messageRef = firebaseDb.getReference("messages")
    private val usersRef = firebaseDb.getReference("users")

    // TODO model은 레포지토리로 바꾸고, 여기서는 response로 받아오는 게 나을 듯 - 리팩토링 1순위
    suspend fun getMyChatList(): Result<List<ChatListModel>> {
        var result: Result<List<ChatListModel>> = Result.Loading
        val userChatList: ArrayList<String> = arrayListOf()
        val chatList: ArrayList<ChatListModel> = arrayListOf()
        if (uid != null) {
            usersChatListRef.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userChatList += snapshot.children.mapNotNull {
                        it.value.toString()
                    }
                    userChatList.forEach {
                        chatRef.child(it).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val feedFrom = snapshot.child("feedFrom").value.toString()
                                val lastMessage = snapshot.child("lastMessage").value.toString()
                                val whenLast = snapshot.child("whenLast").value as Timestamp
                                chatList.add(
                                    ChatListModel(
                                        feedFrom = feedFrom,
                                        lastMessage = lastMessage,
                                        whenLast = whenLast,
                                    )
                                )
                            }

                            override fun onCancelled(error: DatabaseError) {
                                result = Result.Error(error.message)
                            }
                        })
                    }
                    result = Result.Success(chatList)
                }

                override fun onCancelled(error: DatabaseError) {
                    result = Result.Error(error.message)
                }

            })
        }
        return result
    }

    suspend fun getChatPartner(feedId: String): Result<ChatMemberModel> {
        var result: Result<ChatMemberModel> = Result.Loading
        memberRef.child(feedId).get().addOnSuccessListener {
            val uid = it.child("uid").value.toString()
            val nickname = it.child("nickname").value.toString()
            val profileUrl = it.child("profileUrl").value.toString()
            result = Result.Success(ChatMemberModel(uid, nickname, profileUrl))
        }.addOnFailureListener {
            result = Result.Error(it.message.toString())
        }
        return result
    }

    suspend fun sendMessage(feedId: String, targetUid: String, message: String) {
        val feedMessage = messageRef.child(feedId)
        val newMessage = feedMessage.push()
        val messageMap = hashMapOf(
            "message" to message,
            "receiver" to targetUid,
            "sender" to uid,
            "timestamp" to ServerValue.TIMESTAMP,
        )
        newMessage.setValue(messageMap).addOnSuccessListener {
            Timber.d("message success")
        }.addOnFailureListener {
            Timber.d("message fail")
        }
    }

    suspend fun initMessage(feedId: String): List<Result<ChatModel>> {
        val list: ArrayList<Result<ChatModel>> = arrayListOf()
        return suspendCancellableCoroutine<List<Result<ChatModel>>> { continuation ->
            messageRef.child(feedId)
//                .orderByChild("timestamp")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (messageSnapshot in snapshot.children) {
                            val message = messageSnapshot.child("message").value.toString()
                            val receiver = messageSnapshot.child("receiver").value.toString()
                            val sender = messageSnapshot.child("sender").value.toString()
                            val timestamp =
                                Timestamp(Date(messageSnapshot.child("timestamp").value as Long))
                            list.add(
                                Result.Success(
                                    ChatModel(
                                        message = message,
                                        receiver = receiver,
                                        sender = sender,
                                        timestamp = timestamp
                                    )
                                )
                            )
                            Timber.d(
                                "initMessage ele : ${
                                    Result.Success(
                                        ChatModel(
                                            message = message,
                                            receiver = receiver,
                                            sender = sender,
                                            timestamp = timestamp
                                        )
                                    )
                                }"
                            )
                        }
                        Timber.d("initMessages : $list")
                        if (continuation.isActive) {
                            continuation.resume(list)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        list.add(Result.Error(error.message))
                        if (continuation.isActive) {
                            continuation.resume(list)
                        }
                    }
                })
        }
    }

    fun receiveMessage(feedId: String): Flow<Result<ChatModel>> {
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)
        return callbackFlow {
            val listener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Timber.d("onChildAdded 임")
                    val message = snapshot.child("message").value.toString()
                    val receiver = snapshot.child("receiver").value.toString()
                    val sender = snapshot.child("sender").value.toString()
                    val timestamp = Timestamp(Date(snapshot.child("timestamp").value as Long))
                    val nowChatModel = ChatModel(
                        message = message,
                        receiver = receiver,
                        sender = sender,
                        timestamp = timestamp
                    )
                    Timber.tag("snapshot parent").d(snapshot.ref.parent.toString().substringAfterLast("/"))
                    
                    // TODO 나중에 메시지가 처음 시작되면 만들어지는 걸로 변경
                    if (true) {
                        scope.launch {
                            createMembers(feedId, receiver, sender)
                            createChats(
                                chatRef,
                                ChatListModel(
                                    feedFrom = feedId,
                                    lastMessage = message,
                                    whenLast = timestamp
                                ),
                                receiver,
                                sender
                            )
                        }
                    } else {
                        scope.launch {
                            setChats(
                                chatRef,
                                ChatListModel(
                                    feedFrom = feedId,
                                    lastMessage = message,
                                    whenLast = timestamp
                                )
                            )
                        }
                    }
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
            messageRef.child(feedId).orderByChild("timestamp").limitToLast(1)
                .addChildEventListener(listener)
            awaitClose {
                messageRef.child(feedId).orderByChild("timestamp").limitToLast(1)
                    .removeEventListener(listener)
            }
        }
    }

    suspend fun createMembers(feedId: String, receiverUid: String, senderUid: String) {
        val membersFeed = memberRef.child(feedId)
        membersFeed.child(receiverUid).setValue(true)
        membersFeed.child(senderUid).setValue(true)
    }

    suspend fun createChats(
        chatRef: DatabaseReference,
        chatListModel: ChatListModel,
        receiverUid: String,
        senderUid: String,
    ) {
        chatRef.child(chatListModel.feedFrom.toString()).apply {
            child(receiverUid).setValue(true)
            child(senderUid).setValue(true)
        }
        setChats(chatRef, chatListModel)
        usersChatListRef.child(receiverUid).child(chatListModel.feedFrom.toString()).setValue(true)
        usersChatListRef.child(senderUid).child(chatListModel.feedFrom.toString()).setValue(true)
    }

    suspend fun setChats(chatRef: DatabaseReference, chatListModel: ChatListModel) {
        chatRef.child(chatListModel.feedFrom.toString()).apply {
            child("feedFrom").setValue(chatListModel.feedFrom)
            child("lastMessage").setValue(chatListModel.lastMessage)
            child("whenLast").setValue(chatListModel.whenLast)
        }
    }

    fun getChatIdFromUsers() : String {

        return ""
    }

}

//val db = Firebase.database
//// child 가 아래 속성
////        val ref = db.getReference("chats").child("base").child("person")
//// 값 설정
////        ref.child("base").child("person").setValue("상대방 이름(string)")
//val ref = db.getReference("chats")
//val newData = mapOf(
//    "test" to "테스트용",
//)
//
//// 임의의 고정키 이름으로 자식 생성
////        val newChild = ref.push()
//
//// 이름 지정한 자식 생성
//val newChild = ref.child("mytest")
//
//newChild.setValue(newData).addOnSuccessListener {
//    Timber.d("rtdb success")
//}.addOnFailureListener {
//    Timber.d("rtdb fail")
//}
//
//ref.addValueEventListener(object : ValueEventListener {
//    override fun onDataChange(snapshot: DataSnapshot) {
//        val value = snapshot.value
//        // 자식 개수 구하기
////                val count = snapshot.childrenCount
//        Timber.d("rtdb value : $value")
//    }
//
//    override fun onCancelled(error: DatabaseError) {
//        Timber.d("rtdb error : ${error.toException()}")
//    }
//})