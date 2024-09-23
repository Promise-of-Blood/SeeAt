package com.pob.seeat.data.remote

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
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
                if (!feed.exists()) {
                    documentSnapshot.reference.delete()
                    null
                } else result.copy(
                    feedTitle = feed.getString("title") ?: "",
                    feedContent = feed.getString("content") ?: ""
                )
            }
    }

    suspend fun deleteReportedFeed(feedId: String) {
        val reportedFeedRef = firestore.collection("feed").document(feedId)
        val reportedUser = reportedFeedRef.get().await().getDocumentReference("user")
        val reportCount = deleteReportByFeedId(feedId)
        deleteCommentsByFeedId(reportedFeedRef)
        reportedFeedRef.delete().await()
        reportedUser?.update("reportedCount", FieldValue.increment(reportCount))?.await()
    }

    suspend fun deleteReportByFeedId(feedId: String): Long {
        val reportList = firestore.collection(REPORT_FEED_COLLECTION).whereEqualTo("feedId", feedId)
        val reportCount = reportList.count().get(AggregateSource.SERVER).await().count
        val querySnapshot = reportList.get().await()
        val batch = firestore.batch()
        for (document in querySnapshot.documents) {
            batch.delete(document.reference)
        }
        batch.commit().await()
        return reportCount
    }

    private suspend fun deleteCommentsByFeedId(feed: DocumentReference) {
        val commentsRef = feed.collection("comments")
        val commentsSnapshot = commentsRef.get().await()
        val batch = firestore.batch()
        for (document in commentsSnapshot.documents) {
            batch.delete(document.reference)
        }
        batch.commit().await()
    }
}