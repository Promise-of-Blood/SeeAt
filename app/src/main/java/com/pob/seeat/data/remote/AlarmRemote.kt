package com.pob.seeat.data.remote

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.data.model.AlarmResponse
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.AlarmModel
import com.pob.seeat.domain.model.toAlarmModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AlarmRemote @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAlarmList(uId: String): Flow<Result<List<AlarmModel>>> = callbackFlow {
        trySend(Result.Loading)
        val userRef = firestore.collection("user").document(uId)
        val alarmListener = getAlarmRef(uId).addSnapshotListener { snapshot, exception ->
            if (exception != null) trySend(Result.Error("알림 목록을 가져오는데 실패했습니다."))
            else if (snapshot != null) {
                try {
                    val response = snapshot.documents.mapNotNull { documentSnapshot ->
                        val commentPath =
                            documentSnapshot.getString("commentId") ?: return@mapNotNull null
                        val commentRef = firestore.document(commentPath)
                        async {
                            val commentDeferred = async { commentRef.get().await() }
                            val feedDeferred = async { commentRef.parent.parent?.get()?.await() }
                            val commentDocument = commentDeferred.await()
                            val feedDocument = feedDeferred.await()

                            // 삭제된 댓글이거나, 삭제된 글이거나, 현재 로그인 한 유저의 댓글인 경우
                            if (!commentDocument.exists() || feedDocument == null || !feedDocument.exists()
                                || commentDocument.getDocumentReference("user") == userRef
                            ) {
                                getAlarmRef(uId).document(documentSnapshot.id).delete().await()
                                return@async null
                            }

                            AlarmResponse(
                                alarmId = documentSnapshot.id,
                                feedId = feedDocument.id,
                                feedTitle = feedDocument.getString("title"),
                                feedImage = (feedDocument.get("contentImage") as? List<*>)?.firstOrNull() as? String,
                                comment = commentDocument.getString("comment"),
                                timestamp = commentDocument.getTimestamp("timeStamp"),
                                isRead = documentSnapshot.getBoolean("isRead"),
                            )
                        }
                    }
                    launch {
                        val result = response.awaitAll().filterNotNull()
                        trySend(Result.Success(result.map(AlarmResponse::toAlarmModel)))
                    }
                } catch (e: Exception) {
                    trySend(Result.Error("알림 목록을 가져오는데 문제가 발생했습니다."))
                }
            }
        }
        awaitClose { alarmListener.remove() }
    }

    suspend fun readAlarm(uId: String, alarmId: String) {
        getAlarmRef(uId).document(alarmId).update("isRead", true).await()
    }

    suspend fun deleteAlarm(uId: String, alarmId: String) {
        getAlarmRef(uId).document(alarmId).delete().await()
    }

    suspend fun getUnreadAlarmCount(uId: String): Long {
        return getAlarmRef(uId).whereEqualTo("isRead", false).count()
            .get(AggregateSource.SERVER).await().count
    }

    private fun getAlarmRef(uId: String) =
        firestore.collection("user").document(uId).collection("alarm")
}