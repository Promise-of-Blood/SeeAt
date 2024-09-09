package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.pob.seeat.domain.model.CommentReportModel
import javax.inject.Inject

class ReportCommentService @Inject constructor() {

    fun sendReportComment(report: CommentReportModel) {
        val database = Firebase.firestore

        database.collection("report_comment")
            .add(report)
            .addOnSuccessListener { docRef ->
                Log.d("댓글 신고", "문서 ID : ${docRef.id}")

            }
            .addOnFailureListener { e ->
                Log.e("댓글 신고", "댓글 신고 sending 오류 : ${e.message}")
            }
    }
}