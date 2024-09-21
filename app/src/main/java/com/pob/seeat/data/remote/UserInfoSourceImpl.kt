package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pob.seeat.data.model.UserInfoData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserInfoSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : UserInfoSource {

    override suspend fun createUserInfo(userInfoData: UserInfoData) {
        firestore.collection("user").document(userInfoData.uid).set(userInfoData)
            .addOnSuccessListener {
                Log.d("회원가입", "성공")
            }.addOnFailureListener { e ->
                Log.e("회원가입", "실패 ( 에러 : $e )")
            }
    }

    override suspend fun getUserInfo(uid: String): Flow<UserInfoData?> {
        return flow {
            val likedFeedSnapshot =
                firestore.collection("user").document(uid).collection("likedFeed").get().await()
            val likedFeedList = likedFeedSnapshot.documents.map { it.id }
            val userRef = firestore.collection("user").document(uid)
            val feedCount = firestore.collection("feed").whereEqualTo("user", userRef).count()
                .get(AggregateSource.SERVER).await().count // 작성 글 수
            val commentCount =
                firestore.collectionGroup("comments").whereEqualTo("user", userRef).count()
                    .get(AggregateSource.SERVER).await().count // 작성 댓글 수
            val myData = userRef.get().await() // 유저 정보
            emit(
                myData.toObject<UserInfoData>()?.copy(
                    feedCount = feedCount,
                    commentCount = commentCount,
                    likedFeedList = likedFeedList,
                    isAdmin = checkIsAdmin(myData.id),
                )
            )
        }
    }

    override suspend fun deleteUserInfo(userInfoData: UserInfoData) {
        firestore.collection("user").document(userInfoData.uid).delete().await()
    }

    override suspend fun updateUserInfo(userInfoData: UserInfoData) {
        firestore.collection("user").document(userInfoData.uid).set(userInfoData).await()
    }

    override suspend fun getCurrentUserUid(): Flow<String> {
        return flow { emit(firebaseAuth.currentUser?.uid ?: "") }
    }

    override suspend fun getUserInfoByEmail(email: String): Flow<UserInfoData?> {
        return flow {
            val myData = firestore.collection("user").document(email).get().await()
            emit(myData.toObject<UserInfoData>())
        }
    }

    override suspend fun createLikedFeed(userUid: String, feedUid: String) {
        try {
            firestore.collection("user").document(userUid).collection("likedFeed").document(feedUid)
                .set(mapOf("feed" to "/feed/$feedUid")).await()
            Timber.tag("likedFeed 생성").i("성공)")
        } catch (e: Exception) {
            Timber.tag("likedFeed 생성").i("실패 ( 에러: $e)")
        }
    }

    override suspend fun removeLikedFeed(userUid: String, feedUid: String) {
        try {
            firestore.collection("user").document(userUid).collection("likedFeed").document(feedUid)
                .delete().await()

            Timber.tag("likedFeed 제거").i("제거 성공 $feedUid")

        } catch (e: Exception) {
            Timber.tag("likedFeed 제거").i("실패 ( 에러: $e)")
        }
    }

    override suspend fun getUserList(): List<UserInfoData> {
        return firestore.collection("user").get().await().documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject<UserInfoData>()?.copy(
                isAdmin = checkIsAdmin(documentSnapshot.id)
            )
        }.sortedByDescending { it.isAdmin }
    }

    private suspend fun checkIsAdmin(uid: String): Boolean {
        return firestore.collection("admin").document(uid).get().await().exists()
    }
}