package com.pob.seeat.data.remote

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
        val feedDocuments = firestore.collection("feed")
            .whereEqualTo("user", userRef)
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
        val commentRef = firestore.collectionGroup("comments")
            .whereEqualTo("user", userRef)
        if (limit != null) commentRef.limit(limit)
        return commentRef.get().await().documents.mapNotNull { documentSnapshot ->
            val feedDocument = documentSnapshot.reference.parent.parent?.get()?.await()
            documentSnapshot.toObject(CommentHistoryModel::class.java)?.copy(
                feedId = feedDocument?.id ?: "",
                feedTitle = feedDocument?.get("title") as? String ?: "",
                commentId = documentSnapshot.id,
            )
        }
    }

    suspend fun getLikedList(
        uid: String,
        limit: Long? = null,
        startAfter: String? = null
    ): List<FeedModel> {
        val userRef = firestore.collection("user").document(uid ?: "")
        val feedRefs = firestore.collection("like").whereEqualTo("user", userRef)
        if (limit != null) feedRefs.limit(limit)
        if (startAfter != null) feedRefs.startAfter(startAfter)
        return feedRefs.get().await().documents.mapNotNull { documentSnapshot ->
            val feedDocument =
                firestore.collection("feed").document(documentSnapshot.id).get().await()
            val tagList = documentSnapshot.get("tagList") as? List<*>
            feedDocument.toObject(FeedModel::class.java)?.copy(
                feedId = feedDocument.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }
}