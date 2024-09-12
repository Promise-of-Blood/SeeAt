package com.pob.seeat.data.repository

import com.google.firebase.Timestamp

interface ReportCommentRepository {
    fun reportComment(reporterId: String, reportedUserId: String, feedId: String, commentId: String,timestamp: Timestamp)
}