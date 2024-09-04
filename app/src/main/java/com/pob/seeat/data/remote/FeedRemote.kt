package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedRemote @Inject constructor(
    private val firestore: FirebaseFirestore
) : GetFeedList {

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
                    val nickname = (user as? DocumentReference)?.get()?.await()?.getString("nickname") ?: "탈퇴한 사용자"

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
                val userImage = userData?.get("profileUrl") as? String ?: "https://firebasestorage.googleapis.com/v0/b/see-at.appspot.com/o/profile_images%2Fiv_main_icon.png?alt=media&token=33eb6196-76b4-419d-8bc3-f986219b290b"

                // 로그로 nickname 값을 출력하여 확인
                Log.d("FeedRemote", "Fetched nickname: $nickname for user: ${user?.id}")
                copy(
                    nickname = nickname.toString(),
                    userImage = userImage.toString(),
                    )

            }
    }
}