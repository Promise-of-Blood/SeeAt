package com.pob.seeat.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.domain.model.CommentHistoryModel
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserHistoryRemote @Inject constructor(
    private val firestore: FirebaseFirestore
) : GetFeedList {
    override suspend fun getFeedList(
        uid: String?,
        limit: Long?,
        startAfter: String?
    ): List<FeedModel> {
        val userRef = firestore.collection("user").document(uid ?: "")
        val feedDocuments = firestore.collection("feed").whereEqualTo("user", userRef)
        if (limit != null) feedDocuments.limit(limit)
        if (startAfter != null) feedDocuments.startAfter(startAfter)
        return feedDocuments.get().await().documents.mapNotNull { documentSnapshot ->
            val tagList = documentSnapshot.get("tagList") as? List<*>
            documentSnapshot.toObject(FeedModel::class.java)?.copy(
                feedId = documentSnapshot.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }

    suspend fun getCommentList(
        uid: String,
        limit: Long? = null,
        startAfter: String? = null
    ): List<CommentHistoryModel> {
        val userRef = firestore.collection("user").document(uid ?: "")
        val commentsDocuments = firestore.collectionGroup("comments").whereEqualTo("user", userRef)
        val feedDocumentsMap =
            mutableMapOf<String, DocumentSnapshot>() // 같은 글에 여러 개의 댓글을 단 경우 중복 요청 방지
        if (limit != null) commentsDocuments.limit(limit)
        return commentsDocuments.get().await().documents.mapNotNull { documentSnapshot ->
            val feedId = documentSnapshot.reference.parent.parent?.id ?: ""
            val feedDocument =
                feedDocumentsMap[feedId] ?: firestore.collection("feed").document(feedId)
                    .get().await().also { document -> feedDocumentsMap[feedId] = document }
            CommentHistoryModel(
                feedId = feedId,
                feedTitle = feedDocument.getString("title") ?: "",
                commentId = documentSnapshot.id,
                comment = documentSnapshot.getString("comment") ?: "",
                likeCount = documentSnapshot.get("likeCount") as? Int ?: 0,
                timeStamp = documentSnapshot.getTimestamp("timeStamp")
            )
        }
    }

    suspend fun getLikedList(
        uid: String,
        limit: Long? = null,
        startAfter: String? = null
    ): List<FeedModel> {
        val userRef = firestore.collection("user").document(uid ?: "")
        val likedFeedRefs = firestore.collection("user").document(uid ?: "").collection("likedFeed")
        if (limit != null) likedFeedRefs.limit(limit)
        if (startAfter != null) likedFeedRefs.startAfter(startAfter)
        return likedFeedRefs.get().await().documents.mapNotNull { documentSnapshot ->
            val feedDocument = documentSnapshot.getDocumentReference("feed")?.get()?.await()
            feedDocument?.toObject(FeedModel::class.java)?.copy(
                feedId = documentSnapshot.id,
                tags = (documentSnapshot.get("tagList") as? List<*>)?.filterIsInstance<String>()
                    .orEmpty(),
            )
        }
    }
}