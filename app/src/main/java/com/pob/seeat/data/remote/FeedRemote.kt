package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedRemote @Inject constructor(
    private val firestore: FirebaseFirestore
) : GetFeedList {

    override suspend fun getFeedList(uid: String?): List<FeedModel> {
        val feedDocuments = firestore.collection("feed")
            .get()
            .await()
            .documents

        Log.d("FeedRemote", "getFeedList: $feedDocuments")

        return feedDocuments.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(FeedModel::class.java)?.copy(feedId = documentSnapshot.id)
                ?.run {
                    val nickname =
                        user?.get()?.await()?.getString("nickname")

                    // 로그로 nickname 값을 출력하여 확인
                    Log.d("FeedRemote", "Fetched nickname: $nickname for user: ${user?.id}")

                    nickname?.let {
                        copy(nickname = it)
                    } ?: this
                }
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