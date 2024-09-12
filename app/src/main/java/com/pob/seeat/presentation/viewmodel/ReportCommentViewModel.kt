package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.pob.seeat.domain.usecase.ReportCommentUseCase
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportCommentViewModel @Inject constructor(private val reportCommentUseCase: ReportCommentUseCase) : ViewModel() {

    fun sendReport(reportedUserId : String, feedId: String, commentId:String,timestamp: Timestamp){
        var reporterId = getUserUid()
        if(reporterId != null){
            reportCommentUseCase.execute(reporterId, reportedUserId, feedId, commentId,timestamp)
        }else{
            reporterId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            reportCommentUseCase.execute(reporterId, reportedUserId, feedId, commentId,timestamp)
        }
    }


}

