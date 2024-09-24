package com.pob.seeat.data.remote

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pob.seeat.data.model.report.ReportedFeedHistoryResponse
import com.pob.seeat.data.model.report.ReportedFeedResponse
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.model.FeedReportModel
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

private const val REPORT_FEED_COLLECTION = "report_feed"

class ReportFeedRemote @Inject constructor(private val firestore: FirebaseFirestore) :
    SendReportFeed {

    override suspend fun addReportFeed(report: FeedReportModel) {
        val userRef = firestore.collection("user").document(report.reportedUserId ?: "")
        firestore.collection("report_feed").add(report).addOnSuccessListener { docRef ->
            userRef.update("reportedCount", FieldValue.increment(1))
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

    suspend fun getReportedFeedList(uid: String): List<ReportedFeedHistoryResponse> {
        return firestore.collection(REPORT_FEED_COLLECTION).whereEqualTo("reportedUserId", uid)
            .orderBy("timeStamp", Query.Direction.DESCENDING).get().await()
            .groupBy { it.getString("feedId") ?: "" }
            .mapNotNull { (feedId, documentSnapshotList) ->
                val feed = firestore.collection("feed").document(feedId).get().await()
                if (!feed.exists()) {
                    documentSnapshotList.map { it.reference.delete() }
                    null
                } else ReportedFeedHistoryResponse(
                    content = feed.toObject(FeedModel::class.java),
                    timeStamp = documentSnapshotList.first().getTimestamp("timeStamp"),
                    reportedCount = documentSnapshotList.size.toLong(),
                    isDeleted = false,
                )
            }
    }

    suspend fun getReportedFeedHistoryList(uid: String): List<ReportedFeedHistoryResponse> {
        val userRef = firestore.collection("user").document(uid)
        return userRef.collection("reportedFeed")
            .orderBy("timeStamp", Query.Direction.DESCENDING).get().await().mapNotNull {
                ReportedFeedHistoryResponse(
                    content = it.toObject(FeedModel::class.java),
                    timeStamp = it.getTimestamp("deletedAt"),
                    reportedCount = it.getLong("reportedCount") ?: 0,
                    isDeleted = true,
                )
            }
    }

    suspend fun deleteReportedFeed(feedId: String) {
        val reportedFeedRef = firestore.collection("feed").document(feedId)
        val reportedUser = reportedFeedRef.get().await().getDocumentReference("user")
        val reportedCount = deleteReportByFeedId(feedId)

        // feed 데이터 복사
        val reportedFeedSnapshot = reportedFeedRef.get().await()
        val newData = reportedFeedSnapshot.data?.plus(
            mapOf("reportedCount" to reportedCount, "deletedAt" to FieldValue.serverTimestamp())
        )

        deleteCommentsByFeedId(reportedFeedRef)
        reportedFeedRef.delete().await()
        reportedUser?.let {
            if (newData != null) {
                it.update("reportedCount", FieldValue.increment(reportedCount)).await()
                it.collection("reportedFeed").document(feedId).set(newData).await()
            }
        }
    }

    suspend fun deleteReportByFeedId(feedId: String, isIgnore: Boolean = true): Long {
        val reportList = firestore.collection(REPORT_FEED_COLLECTION).whereEqualTo("feedId", feedId)
        val reportCount = reportList.count().get(AggregateSource.SERVER).await().count
        val querySnapshot = reportList.get().await()
        var userRef: DocumentReference? = null
        val batch = firestore.batch()
        for (document in querySnapshot.documents) {
            if (userRef == null) userRef = document.getDocumentReference("user")
            batch.delete(document.reference)
        }
        batch.commit().await()
        if (isIgnore) userRef?.update("reportedCount", FieldValue.increment(-reportCount))
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