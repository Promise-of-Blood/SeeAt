package com.pob.seeat.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.domain.model.FeedReportModel
import timber.log.Timber
import javax.inject.Inject

class ReportFeedRemote @Inject constructor(private val firestore: FirebaseFirestore) :
    SendReportFeed {

    override suspend fun addReportFeed(report: FeedReportModel) {
        firestore.collection("report_feed")
            .add(report)
            .addOnSuccessListener { docRef ->
                Timber.i("댓글신고 문서ID ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Timber.i("댓글신고 add오류 : $e")
            }
    }
}