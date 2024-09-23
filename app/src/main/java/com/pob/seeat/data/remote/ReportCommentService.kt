package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.pob.seeat.data.model.report.ReportedCommentResponse
import com.pob.seeat.domain.model.CommentReportModel
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

private const val REPORT_COMMENT_TAG = "댓글 신고"
private const val REPORT_COMMENT_COLLECTION = "report_comment"

class ReportCommentService @Inject constructor(private val firestore: FirebaseFirestore) {

    fun sendReportComment(report: CommentReportModel) {
        val database = Firebase.firestore

        database.collection("report_comment").add(report).addOnSuccessListener { docRef ->
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

    suspend fun deleteReportedComment(feedId: String, commentId: String) {
        val reportedCommentRef =
            firestore.collection("feed").document(feedId).collection("comments").document(commentId)
        val reportedUser = reportedCommentRef.get().await().getDocumentReference("user")
        val reportCount = deleteReportByCommentId(commentId)
        reportedCommentRef.delete().await()
        reportedUser?.update("reportedCount", FieldValue.increment(reportCount))
    }

    suspend fun deleteReportByCommentId(commentId: String): Long {
        val reportList =
            firestore.collection(REPORT_COMMENT_COLLECTION).whereEqualTo("commentId", commentId)
        val reportCount = reportList.count().get(AggregateSource.SERVER).await().count
        reportList.get().addOnSuccessListener { querySnapshot ->
            val batch = firestore.batch()
            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit().addOnFailureListener { e ->
                Timber.e("신고 삭제 오류: $e")
            }
        }.addOnFailureListener { e ->
            Timber.e("신고 목록 불러오기 오류: $e")
        }
        return reportCount
    }
}