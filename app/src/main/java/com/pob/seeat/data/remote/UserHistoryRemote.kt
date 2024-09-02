package com.pob.seeat.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserHistoryRemote @Inject constructor(
    private val firestore: FirebaseFirestore
) : GetFeedList {
    override suspend fun getFeedList(uid: String?): List<FeedModel> {
        val feedDocuments = firestore.collection("feed")
            .whereEqualTo("user", uid).get().await().documents
        return feedDocuments.mapNotNull { documentSnapshot ->
            val tagList = documentSnapshot.get("tagList") as? List<*>
            documentSnapshot.toObject(FeedModel::class.java)?.copy(
                feedId = documentSnapshot.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }

    suspend fun getCommentList(uid: String): List<FeedModel> {
        val feedDocuments = firestore.collection("feed").get().await().documents
        val commentedFeedDocuments = feedDocuments.filter { feedDocument ->
            feedDocument.reference.collection("comments")
                .whereEqualTo("user", uid).get().await().documents.isNotEmpty()
        }
        return commentedFeedDocuments.mapNotNull { documentSnapshot ->
            val tagList = documentSnapshot.get("tagList") as? List<*>
            val commentsDocuments = documentSnapshot.reference.collection("comments")
                .whereEqualTo("user", uid).get().await().documents
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

    suspend fun getLikedList(uid: String): List<FeedModel> {
        // TODO 좋아요 한 글 가져오기
        val feedDocuments = firestore.collection("feed")
            .whereEqualTo("user", uid).get().await().documents
        return feedDocuments.mapNotNull { documentSnapshot ->
            val tagList = documentSnapshot.get("tagList") as? List<*>
            documentSnapshot.toObject(FeedModel::class.java)?.copy(
                feedId = documentSnapshot.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }
}