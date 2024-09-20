package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.usecase.ReportedCommentListUseCase
import com.pob.seeat.domain.usecase.ReportedFeedListUseCase
import com.pob.seeat.presentation.view.admin.items.AdminListItem
import com.pob.seeat.presentation.view.admin.items.toCommentAdminListItem
import com.pob.seeat.presentation.view.admin.items.toFeedAdminListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val reportedCommentListUseCase: ReportedCommentListUseCase,
    private val reportedFeedListUseCase: ReportedFeedListUseCase,
) : ViewModel() {
    var commentList = MutableStateFlow<Result<List<AdminListItem>>>(Result.Loading)
        private set
    var feedList = MutableStateFlow<Result<List<AdminListItem>>>(Result.Loading)
        private set
    var reportedList = MutableStateFlow<Result<List<AdminListItem>>>(Result.Loading)
        private set

    fun getReportedCommentList() {
        viewModelScope.launch {
            reportedCommentListUseCase().collect {
                when (it) {
                    is Result.Success -> {
                        commentList.value = Result.Success(it.data.toCommentAdminListItem())
                    }

                    is Result.Error -> {
                        commentList.value = Result.Error(it.message)
                    }

                    is Result.Loading -> {
                        commentList.value = Result.Loading
                    }
                }
            }
        }
    }

    fun getReportedFeedList() {
        viewModelScope.launch {
            reportedFeedListUseCase().collect {
                when (it) {
                    is Result.Success -> {
                        feedList.value = Result.Success(it.data.toFeedAdminListItem())
                    }

                    is Result.Error -> {
                        feedList.value = Result.Error(it.message)
                    }

                    is Result.Loading -> {
                        feedList.value = Result.Loading
                    }
                }
            }
        }
    }

//    fun getReportedList() {
//        viewModelScope.launch {
//            reportedCommentListUseCase().combine(reportedFeedListUseCase()) { comment, feed ->
//                listOf(comment, feed)
//            }.collect { reportedList.value = it. }
//        }
//    }

    init {
//        getReportedCommentList()
        getReportedFeedList()
    }
}