package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.domain.usecase.DeleteAllUserInfoUseCase
import com.pob.seeat.domain.usecase.GetUserListUseCase
import com.pob.seeat.domain.usecase.ReportedCommentHistoryListUseCase
import com.pob.seeat.domain.usecase.ReportedFeedHistoryListUseCase
import com.pob.seeat.domain.usecase.UpdateIsAdminUseCase
import com.pob.seeat.domain.usecase.UserInfoUseCases
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.admin.items.AdminListItem
import com.pob.seeat.presentation.view.admin.items.AdminReportListItem
import com.pob.seeat.presentation.view.admin.items.toCommentAdminReportListItem
import com.pob.seeat.presentation.view.admin.items.toFeedAdminReportListItem
import com.pob.seeat.presentation.view.admin.items.toUserAdminListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminUserViewModel @Inject constructor(
    private val getUserListUseCase: GetUserListUseCase,
    private val userInfoUseCases: UserInfoUseCases,
    private val updateIsAdminUseCase: UpdateIsAdminUseCase,
    private val deleteAllUserInfoUseCase: DeleteAllUserInfoUseCase,
    private val reportedCommentHistoryListUseCase: ReportedCommentHistoryListUseCase,
    private val reportedFeedHistoryListUseCase: ReportedFeedHistoryListUseCase,
) : ViewModel() {
    var userList = MutableStateFlow<Result<List<AdminListItem>>>(Result.Loading)
        private set
    var userDetail = MutableStateFlow<UiState<UserInfoModel>>(UiState.Loading)
        private set
    var reportedList = MutableStateFlow<Result<List<AdminReportListItem>>>(Result.Loading)
        private set

    fun getUserList() {
        viewModelScope.launch {
            getUserListUseCase().collect {
                userList.value = when (it) {
                    is Result.Loading -> Result.Loading
                    is Result.Success -> Result.Success(it.data.toUserAdminListItem())
                    is Result.Error -> Result.Error(it.message)
                }
            }
        }
    }

    fun getUserDetail(uid: String) {
        viewModelScope.launch {
            userDetail.value = UiState.Loading
            val currentUserInfo = userInfoUseCases.getUserInfoUseCase.execute(uid)
            userDetail.value =
                if (currentUserInfo != null) UiState.Success(currentUserInfo) else UiState.Error("유저정보를 찾을 수 없습니다.")
        }
    }

    fun getReportedList(uid: String) {
        viewModelScope.launch {
            combine(
                reportedCommentHistoryListUseCase(uid), reportedFeedHistoryListUseCase(uid)
            ) { comment, feed ->
                when {
                    comment is Result.Success && feed is Result.Success -> {
                        Result.Success(
                            mergeSortedLists(
                                comment.data.toCommentAdminReportListItem(),
                                feed.data.toFeedAdminReportListItem(),
                            )
                        )
                    }

                    comment is Result.Success && feed !is Result.Success -> {
                        Result.Success(comment.data.toCommentAdminReportListItem())
                    }

                    comment !is Result.Success && feed is Result.Success -> {
                        Result.Success(feed.data.toFeedAdminReportListItem())
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

    fun updateIsAdmin(uid: String, isAdmin: Boolean) {
        viewModelScope.launch {
            updateIsAdminUseCase(uid, isAdmin)
            getUserList()
        }
    }

    fun deleteUser(uid: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            deleteAllUserInfoUseCase(uid).collect {
                when (it) {
                    is Result.Loading -> {}
                    is Result.Error -> onError(it.message)
                    is Result.Success -> {
                        onSuccess(it.data)
                        getUserList()
                    }
                }
            }
        }
    }

    init {
        getUserList()
    }

    private fun mergeSortedLists(
        commentList: List<AdminReportListItem>, feedList: List<AdminReportListItem>
    ): List<AdminReportListItem> {
        val mergedList = mutableListOf<AdminReportListItem>()
        var commentIndex = 0
        var feedIndex = 0

        while (commentIndex < commentList.size && feedIndex < feedList.size) {
            val commentDate = (commentList[commentIndex] as AdminReportListItem.CommentReport).date
            val feedDate = (feedList[feedIndex] as AdminReportListItem.FeedReport).date

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