package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedRemote @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getFeedList(): List<FeedModel> {
        val feedDocuments = firestore.collection("feed")
            .get()
            .await()
            .documents

        Log.d("FeedRemote", "getFeedList: ${feedDocuments}")

        // 각 DocumentSnapshot을 FeedModel로 변환하며 ID를 포함
        return feedDocuments.map { documentSnapshot ->
            documentSnapshot.toObject(FeedModel::class.java)?.copy(feedId = documentSnapshot.id)
        }.filterNotNull()
    }

    suspend fun getFeedList(uid: String): List<FeedModel> {
        val feedDocuments = firestore.collection("feed")
            .whereEqualTo("user", uid)
            .get()
            .await()
            .documents

        return feedDocuments.mapNotNull { documentSnapshot ->
            val tagList = documentSnapshot.get("tagList") as? List<*>
            documentSnapshot.toObject(FeedModel::class.java)?.copy(
                feedId = documentSnapshot.id,
                tags = tagList?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }

    suspend fun getFeedById(postId: String): FeedModel? {
        val documentSnapshot = firestore.collection("feed")
            .document(postId)
            .get()
            .await()
        return documentSnapshot.toObject(FeedModel::class.java)
    }
}