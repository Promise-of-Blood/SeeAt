package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.pob.seeat.data.model.report.ReportedCommentResponse
import com.pob.seeat.domain.model.CommentReportModel
import kotlinx.coroutines.tasks.await
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
                val comment =
                    commentDocumentsMap[result.commentId] ?: firestore.collection("comment")
                        .document(result.commentId).get().await().also {
                            commentDocumentsMap[result.commentId] = it
                        }
                result.copy(comment = comment.getString("comment"))
            }
    }
}