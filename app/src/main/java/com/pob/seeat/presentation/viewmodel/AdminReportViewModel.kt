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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminReportViewModel @Inject constructor(
    private val reportedCommentListUseCase: ReportedCommentListUseCase,
    private val reportedFeedListUseCase: ReportedFeedListUseCase,
) : ViewModel() {
    var reportedList = MutableStateFlow<Result<List<AdminListItem>>>(Result.Loading)
        private set

    fun getReportedList() {
        viewModelScope.launch {
            combine(reportedCommentListUseCase(), reportedFeedListUseCase()) { comment, feed ->
                when {
                    comment is Result.Success && feed is Result.Success -> {
                        Result.Success(
                            mergeSortedLists(
                                comment.data.toCommentAdminListItem(),
                                feed.data.toFeedAdminListItem()
                            )
                        )
                    }

                    comment is Result.Success && feed !is Result.Success -> {
                        Result.Success(comment.data.toCommentAdminListItem())
                    }

                    comment !is Result.Success && feed is Result.Success -> {
                        Result.Success(feed.data.toFeedAdminListItem())
                    }

                    comment is Result.Error -> Result.Error(comment.message)
                    feed is Result.Error -> Result.Error(feed.message)
                    else -> Result.Loading
                }
            }.collect {
                reportedList.value = it
            }
        }
    }

    init {
        getReportedList()
    }

    private fun mergeSortedLists(
        commentList: List<AdminListItem>, feedList: List<AdminListItem>
    ): List<AdminListItem> {
        val mergedList = mutableListOf<AdminListItem>()
        var commentIndex = 0
        var feedIndex = 0

        while (commentIndex < commentList.size && feedIndex < feedList.size) {
            val commentDate =
                (commentList[commentIndex] as AdminListItem.CommentReport).recentReportedAt
            val feedDate = (feedList[feedIndex] as AdminListItem.FeedReport).recentReportedAt

            if (commentDate >= feedDate) {
                mergedList.add(commentList[commentIndex])
                commentIndex++
            } else {
                mergedList.add(feedList[feedIndex])
                feedIndex++
            }
        }

        while (commentIndex < commentList.size) {
            mergedList.add(commentList[commentIndex])
            commentIndex++
        }

        while (feedIndex < feedList.size) {
            mergedList.add(feedList[feedIndex])
            feedIndex++
        }

        return mergedList
    }
}