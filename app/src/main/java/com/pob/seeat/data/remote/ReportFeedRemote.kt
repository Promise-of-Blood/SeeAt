package com.pob.seeat.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pob.seeat.data.model.report.ReportedFeedResponse
import com.pob.seeat.domain.model.FeedReportModel
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

private const val REPORT_FEED_COLLECTION = "report_feed"

class ReportFeedRemote @Inject constructor(private val firestore: FirebaseFirestore) :
    SendReportFeed {

    override suspend fun addReportFeed(report: FeedReportModel) {
        firestore.collection("report_feed").add(report).addOnSuccessListener { docRef ->
            Timber.i("댓글신고 문서ID ${docRef.id}")
        }.addOnFailureListener { e ->
            Timber.i("댓글신고 add오류 : $e")
        }
    }

    suspend fun getReportedFeedList(): List<ReportedFeedResponse> {
        val feedDocumentsMap = mutableMapOf<String, DocumentSnapshot>()
        return firestore.collection(REPORT_FEED_COLLECTION)
            .orderBy("timeStamp", Query.Direction.DESCENDING).get().await()
            .mapNotNull { documentSnapshot ->
                val result = documentSnapshot.toObject(ReportedFeedResponse::class.java)
                val feed = feedDocumentsMap[result.feedId] ?: firestore.collection("feed")
                    .document(result.feedId).get().await().also {
                        feedDocumentsMap[result.feedId] = it
                    }
                result.copy(
                    feedTitle = feed.getString("title") ?: "",
                    feedContent = feed.getString("content") ?: ""
                )
            }
    }
}