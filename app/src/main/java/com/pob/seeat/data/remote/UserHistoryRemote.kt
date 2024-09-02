package com.pob.seeat.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.domain.model.CommentModel
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
        val userRef = firestore.collection("user").document(uid ?: "").get().await().reference
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
    ): List<FeedModel> {
        val userRef = firestore.collection("user").document(uid ?: "").get().await().reference
        val feedDocuments = firestore.collection("feed").get().await().documents
        var commentedFeedDocuments = feedDocuments.filter { feedDocument ->
            feedDocument.reference.collection("comments")
                .whereEqualTo("user", userRef).get().await().documents.isNotEmpty()
        }
        if (limit != null) commentedFeedDocuments = commentedFeedDocuments.take(limit.toInt())
        return commentedFeedDocuments.mapNotNull { documentSnapshot ->
            val tagList = documentSnapshot.get("tagList") as? List<*>
            val commentsDocuments = documentSnapshot.reference.collection("comments")
                .whereEqualTo("user", userRef).get().await().documents
            documentSnapshot.toObject(FeedModel::class.java)?.copy(
                feedId = documentSnapshot.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList(),
                comments = commentsDocuments.mapNotNull { commentDocument ->
                    commentDocument.toObject(CommentModel::class.java)
                        ?.copy(commentId = commentDocument.id)
                }
            )
        }
    }

    suspend fun getLikedList(
        uid: String,
        limit: Long? = null,
        startAfter: String? = null
    ): List<FeedModel> {
        val userRef = firestore.collection("user").document(uid ?: "").get().await().reference
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