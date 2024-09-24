package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.pob.seeat.data.model.CommentData
import com.pob.seeat.data.model.report.ReportedCommentHistoryResponse
import com.pob.seeat.data.model.report.ReportedCommentResponse
import com.pob.seeat.domain.model.CommentReportModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val REPORT_COMMENT_TAG = "댓글 신고"
private const val REPORT_COMMENT_COLLECTION = "report_comment"

class ReportCommentService @Inject constructor(private val firestore: FirebaseFirestore) {

    fun sendReportComment(report: CommentReportModel) {
        val database = Firebase.firestore
        val userRef = database.collection("user").document(report.reportedUserId)

        database.collection("report_comment").add(report).addOnSuccessListener { docRef ->
            userRef.update("reportedCount", FieldValue.increment(1))
            Log.d(REPORT_COMMENT_TAG, "문서 ID : ${docRef.id}")
        }.addOnFailureListener { e ->
            Log.e(REPORT_COMMENT_TAG, "댓글 신고 sending 오류 : ${e.message}")
        }
    }

    suspend fun getReportedCommentList(): List<ReportedCommentResponse> {
        val commentDocumentsMap = mutableMapOf<String, DocumentSnapshot>()
        return firestore.collection(REPORT_COMMENT_COLLECTION)
            .orderBy("timeStamp", Query.Direction.DESCENDING).get().await()
            .mapNotNull { documentSnapshot ->
                val result = documentSnapshot.toObject(ReportedCommentResponse::class.java)
                val comment = commentDocumentsMap[result.commentId] ?: firestore.collection("feed")
                    .document(result.feedId).collection("comments").document(result.commentId).get()
                    .await().also {
                        commentDocumentsMap[result.commentId] = it
                    }
                if (!comment.exists()) {
                    documentSnapshot.reference.delete()
                    null
                } else result.copy(comment = comment.getString("comment") ?: "")
            }
    }

    suspend fun getReportedCommentList(uid: String): List<ReportedCommentHistoryResponse> {
        return firestore.collection(REPORT_COMMENT_COLLECTION).whereEqualTo("reportedUserId", uid)
            .orderBy("timeStamp", Query.Direction.DESCENDING).get().await()
            .groupBy { it.getString("commentId") ?: "" }
            .mapNotNull { (commentId, documentSnapshotList) ->
                val feedId = documentSnapshotList.first().getString("feedId") ?: ""
                val comment = firestore.collection("feed").document(feedId)
                    .collection("comments").document(commentId).get().await()

                if (!comment.exists()) {
                    documentSnapshotList.map { it.reference.delete() }
                    null
                } else ReportedCommentHistoryResponse(
                    content = comment.toObject(CommentData::class.java),
                    timeStamp = documentSnapshotList.first().getTimestamp("timeStamp"),
                    reportedCount = documentSnapshotList.size.toLong(),
                    isDeleted = false,
                )
            }
    }

    suspend fun getReportedCommentHistoryList(uid: String): List<ReportedCommentHistoryResponse> {
        val userRef = firestore.collection("user").document(uid)
        return userRef.collection("reportedComment")
            .orderBy("timeStamp", Query.Direction.DESCENDING).get().await().mapNotNull {
                ReportedCommentHistoryResponse(
                    content = it.toObject(CommentData::class.java),
                    timeStamp = it.getTimestamp("deletedAt"),
                    reportedCount = it.getLong("reportedCount") ?: 0,
                    isDeleted = true,
                )
            }
    }

    suspend fun deleteReportedComment(feedId: String, commentId: String) {
        val relatedFeedRef = firestore.collection("feed").document(feedId)
        val reportedCommentRef = relatedFeedRef.collection("comments").document(commentId)
        val reportedUser = reportedCommentRef.get().await().getDocumentReference("user")
        val reportedCount = deleteReportByCommentId(commentId, false)

        // comment 데이터 복사
        val reportedCommentSnapshot = reportedCommentRef.get().await()
        val feedTitle = relatedFeedRef.get().await().getString("title") ?: ""
        val newData = reportedCommentSnapshot.data?.plus(
            mapOf(
                "reportedCount" to reportedCount,
                "deletedAt" to FieldValue.serverTimestamp(),
            )
        )

        reportedCommentRef.delete().await()
        reportedUser?.let {
            newData?.let { data ->
                it.collection("reportedComment").document(commentId).set(data).await()
            }
        }
    }

    suspend fun deleteReportByCommentId(commentId: String, isIgnore: Boolean = true): Long {
        val reportList =
            firestore.collection(REPORT_COMMENT_COLLECTION).whereEqualTo("commentId", commentId)
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
}