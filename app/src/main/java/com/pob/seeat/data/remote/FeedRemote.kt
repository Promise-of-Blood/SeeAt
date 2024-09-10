package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.data.model.chat.ChatFeedInfoModel
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedRemote @Inject constructor(
    private val firestore: FirebaseFirestore
) : GetFeedList, DetailFeed {

    override suspend fun getFeedList(
        uid: String?,
        limit: Long?,
        startAfter: String?
    ): List<FeedModel> {
        val feedDocuments = firestore.collection("feed")
            .get()
            .await()
            .documents

        Log.d("FeedRemote", "getFeedList: $feedDocuments")

        return feedDocuments.mapNotNull { documentSnapshot ->
            val tagList = documentSnapshot.get("tagList") as? List<*>

            documentSnapshot.toObject(FeedModel::class.java)?.copy(
                feedId = documentSnapshot.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList()
            )?.run {
                val nickname =
                    (user as? DocumentReference)?.get()?.await()?.getString("nickname") ?: "탈퇴한 사용자"

                // 로그로 nickname 값을 출력하여 확인
                Log.d("FeedRemote", "Fetched nickname: $nickname for user: ${user?.id}")

                nickname?.let {
                    copy(nickname = it)
                } ?: this
            }
        }
    }

    override suspend fun getFeedById(postId: String): FeedModel? {
        val documentSnapshot = firestore.collection("feed")
            .document(postId)
            .get()
            .await()
        val commentsSnapshot =
            firestore.collection("feed").document(postId).collection("comments").get().await()
        val comments =
            commentsSnapshot.documents.mapNotNull { it.toObject(CommentModel::class.java) }

        val tagList = documentSnapshot.get("tagList") as? List<*>
        val userRef = documentSnapshot.getDocumentReference("user")

        return documentSnapshot.toObject(FeedModel::class.java)?.copy(
            user = userRef,
            feedId = documentSnapshot.id,
            tags = tagList?.filterIsInstance<String>() ?: emptyList(),
            comments = comments
        )?.run {
            val userDocument = (user as? DocumentReference)?.get()?.await()
            val userData = userDocument?.data
            val nickname = userData?.get("nickname") as? String ?: "탈퇴한 사용자"
            val userImage = userData?.get("profileUrl") as? String
                ?: "https://firebasestorage.googleapis.com/v0/b/see-at.appspot.com/o/profile_images%2Fiv_main_icon.png?alt=media&token=33eb6196-76b4-419d-8bc3-f986219b290b"

            // 로그로 nickname 값을 출력하여 확인
            Log.d("FeedRemote", "Fetched nickname: $nickname for user: ${user?.id}")
            copy(
                nickname = nickname.toString(),
                userImage = userImage.toString(),
            )

        }
    }

    suspend fun getUserByFeedId(feedId: String) : ChatFeedInfoModel {
        val feedUserRef = firestore.collection("feed")
            .document(feedId)
            .get()
            .await()
            .getDocumentReference("user")
        return ChatFeedInfoModel(
            nickname = feedUserRef?.get()?.await()?.getString("nickname") ?: "(알 수 없음)",
            profileUrl = feedUserRef?.get()?.await()?.getString("profileUrl"),
        )
    }

    suspend fun getFeedListById(feedIdList: List<String>): List<FeedModel> {
        val feedCollection = firestore.collection("feed")
        val feedDocuments = mutableListOf<DocumentSnapshot>()

        // ID 리스트를 10개씩 나눠서 여러 쿼리 실행 (whereIn은 한 번에 10개씩 가져올 수 있음)
        feedIdList.chunked(10).forEach { idsChunk ->
            val querySnapshot = feedCollection
                .whereIn(FieldPath.documentId(), idsChunk)
                .get().await()
            feedDocuments.addAll(querySnapshot.documents)
        }
        return feedDocuments.mapNotNull { documentSnapshot ->
            val userDocument = documentSnapshot.getDocumentReference("user")?.get()?.await()
            val tagList = documentSnapshot.get("tagList") as? List<*>
            val imageList = documentSnapshot.get("contentImage") as? List<*>
            documentSnapshot.toObject(FeedModel::class.java)?.copy(
                nickname = userDocument?.getString("nickname") ?: "탈퇴한 사용자",
                feedId = documentSnapshot.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList(),
                contentImage = imageList?.filterIsInstance<String>() ?: emptyList(),
            )
        }
    }

    override suspend fun updateLikePlus(postId: String) {
        firestore.collection("feed")
            .document(postId)
            .update("like", FieldValue.increment(1))
            .await()
    }

    override suspend fun updateLikeMinus(postId: String) {
        firestore.collection("feed")
            .document(postId)
            .update("like", FieldValue.increment(-1))
            .await()
    }


}